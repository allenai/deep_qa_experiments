package org.allenai.deep_qa.data

import scala.collection.mutable
import scala.collection.JavaConverters._

import com.opencsv.CSVReader
import com.mattg.util.FileUtil
import java.io.StringReader
import org.json4s._
import org.json4s.native.JsonMethods.parse
import org.allenai.deep_qa.parse.Parser

/*
 * Read an Omnibus direct answer file pulled from the Aristo
 * evaluation framework.
 */

class OmnibusDaDatasetReader extends DatasetReader[DirectAnswerInstance] {
  override def readFile(filename: String): Dataset[DirectAnswerInstance] = {
    val fileUtil = new FileUtil
    val reader = new StringReader(fileUtil.readFileContents(filename))
    val csv = new CSVReader(reader)
    val instanceTuples = for {
      line <- csv.readAll().asScala.tail
      questionText = line(3)
      answerText = line(4)
    } yield (questionText, answerText)
    val instances = instanceTuples.map { case (questionText, answerText) => {
      DirectAnswerInstance(questionText, Some(answerText))
    }}
    Dataset(instances)
  }

  def imperativeToInterrogative(inputImperative: String): String = {
    val imperativeSentences = Parser.stanford.splitSentences(inputImperative)
    val imperatives = """identify|name|define|give|explain|describe|list"""
    val interrogatives = """who|what|when|where|why|how"""

    val imperativeModifierRemovedSentences = for {
      imperativeSentence <- imperativeSentences

      // If there is an imperative, remove all words that occur before
      // it (the first imperative) in the sentence. Thus, "Please briefly explain this." becomes
      // "explain this."
      imperativeModifiers = s"""(?i).+?(?=(${imperatives}) )""".r
      imperativeModifierRemovedSentence = imperativeModifiers.replaceFirstIn(imperativeSentence, "")
    } yield imperativeModifierRemovedSentence
    // For each sentence, the imperative words will be replaced with "What is" if
    // they occur at the start of the sentence. However, if these
    // imperative words occur directly before the interrogative words, we just
    // delete the imperative words.
    val imperativesBeforeInterrogatives = s"""(?i)^((${imperatives})+) (?=${interrogatives})""".r
    val generalImperatives = s"""(?i)^((${imperatives})+)""".r

    val transformedImperativeSentences = for {
      imperativeSentence <- imperativeModifierRemovedSentences
      // First, if imperative words occur before interrogative words, delete the imperative
      imperativeSentenceNoImperativesBeforeInterrogatives = imperativesBeforeInterrogatives.replaceFirstIn(
        imperativeSentence,
        ""
      )

      // If the imperative words are still there, it means that they didn't occur
      // before interrogative words and thus we can replace them with "what is"
      interrogativeSentence = generalImperatives.replaceFirstIn(
        imperativeSentenceNoImperativesBeforeInterrogatives,
        "what is"
      )

      // now, strip the excess whitespace on both sides and capitalize
      cleanedInterrogativeSentence = interrogativeSentence.trim.capitalize
    } yield cleanedInterrogativeSentence
    val transformedImperativePassage = transformedImperativeSentences.mkString(" ")
    return transformedImperativePassage
  }
}
