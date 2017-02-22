package org.allenai.deep_qa.data

import scala.collection.mutable
import scala.collection.JavaConverters._

import com.opencsv.CSVReader
import com.mattg.util.FileUtil
import java.io.StringReader
import org.json4s._
import org.json4s.native.JsonMethods.parse

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
}
