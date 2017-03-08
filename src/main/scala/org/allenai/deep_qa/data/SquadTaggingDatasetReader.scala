package org.allenai.deep_qa.data

import scala.collection.mutable

import com.mattg.util.FileUtil
import com.mattg.util.JsonHelper
import org.json4s._
import org.json4s.native.JsonMethods.parse

import org.allenai.deep_qa.parse.StanfordParser

/**
 * This DatasetReader reads in a SQuAD-formatted dataset and produces Instances that are suitable
 * for a tagging model, where what you are trying to tag is whether each span in the passage is
 * something you might ask a question about.
 *
 * We can either output a collection of character spans for each passage, or tokenize the passage
 * using a parser and output a pre-tokenized token-tagged Instance.
 */
class SquadTaggingDatasetReader(
  params: JValue
) extends DatasetReader[BaseTaggingInstance] {
  val tokenize = JsonHelper.extractWithDefault(params, "tokenize", true)

  lazy val fileUtil = new FileUtil
  lazy val parser = new StanfordParser()
  override def readFile(filename: String): Dataset[BaseTaggingInstance] = {
    val json = parse(fileUtil.readFileContents(filename))
    val instanceTuples = for {
      JObject(article) <- json \ "data"
      JField("paragraphs", JArray(paragraphs)) <- article
      JObject(paragraph_jval) <- paragraphs
      JField("context", JString(paragraph)) <- paragraph_jval
      JField("qas", JArray(questions)) <- paragraph_jval
    } yield (paragraph.replace("\n", " "), questions)

    val instances = instanceTuples.par.map { case (paragraph, questions) => {
      val answerTuples = for {
        JObject(question_jval) <- questions
        JField("question", JString(question)) <- question_jval
        JField("answers", JArray(answers)) <- question_jval
        JObject(answer_jval) <- answers
        JField("answer_start", JInt(answerStart)) <- answer_jval
        JField("text", JString(answerText)) <- answer_jval
      } yield (answerStart.toInt, answerText)
      val uniqueAnswers = answerTuples.groupBy(identity).mapValues(_.size).toList.sortBy(-_._2).map(_._1)
      val keptAnswers = resolveConflicts(uniqueAnswers)
      if (!tokenize) {
        val spanIndices = keptAnswers.map { case (spanStart, spanText) => { (spanStart, spanStart + spanText.size) }}
        SequenceTaggingInstance(paragraph, Some(Map("answers" -> spanIndices).toString))
      } else {
        tagPassage(paragraph, keptAnswers)
      }
    }}
    Dataset(instances.seq)
  }

  /**
   * Takes a sequence of spans from a passage, specified as (start position, text), and removes
   * overlaps.  If there's a subset relationship, we keep the longer one.  If there's a partial
   * overlap, we pick semi-randomly (whichever one is longer).
   */
  def resolveConflicts(spans: Seq[(Int, String)]): Seq[(Int, String)] = {
    // Biggest spans first; then we'll add them to a set and just ignore later spans that fully or
    // partially overlap with already-kept spans.
    val sortedSpans = spans.sortBy(-_._2.size)
    val keptSpans = new mutable.HashSet[(Int, String)]
    for (candidateSpan <- sortedSpans) {
      val candidateSpanStart = candidateSpan._1
      val candidateSpanEnd = candidateSpanStart + candidateSpan._2.size
      val overlaps = keptSpans.map { case (spanStart, spanText) => {
        val spanEnd = spanStart + spanText.size
        val beginOverlaps = candidateSpanStart >= spanStart && candidateSpanStart <= spanEnd
        val endOverlaps = candidateSpanEnd >= spanStart && candidateSpanStart <= spanEnd
        beginOverlaps || endOverlaps
      }}
      if (!overlaps.foldLeft(false)(_ || _)) {
        keptSpans += candidateSpan
      }
    }
    keptSpans.toList.sortBy(_._1)
  }

  def tagPassage(passage: String, spans: Seq[(Int, String)]): PreTokenizedSequenceTaggingInstance = {
    val parsedSentences = parser.parseSentences(passage)
    val allTokens = parsedSentences.flatMap(_.tokens)
    var remainingSpans = spans
    val finalTokens = new mutable.ArrayBuffer[String]
    val finalTags = new mutable.ArrayBuffer[String]
    for (token <- allTokens) {
      finalTokens += token.word
      val tokenSpanBegin = token.spanBegin.get
      if (remainingSpans.size == 0) {
        finalTags += "O"
      } else if (tokenSpanBegin < remainingSpans.head._1) {
        finalTags += "O"
      } else if (tokenSpanBegin == remainingSpans.head._1) {
        finalTags += "B"
      } else if (tokenSpanBegin < remainingSpans.head._1 + remainingSpans.head._2.size) {
        finalTags += "I"
      } else {
        finalTags += "O"
        remainingSpans = remainingSpans.drop(1)
      }
    }
    PreTokenizedSequenceTaggingInstance(finalTokens.toSeq, Some(finalTags.toSeq))
  }
}
