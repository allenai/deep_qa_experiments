package org.allenai.deep_qa.data

import com.mattg.util.FileUtil
import org.json4s._

import scala.collection.mutable

case class Dataset[T <: Instance](instances: Seq[T]) {
  def writeToFiles(filenames: Seq[String], withIndices: Boolean, fileUtil: FileUtil) {
    // Each Instance returns a Seq[Seq[String]].  This call flattens together all of the inner
    // Seq[String]s, giving one list of Seq[Seq[String]] with all of the instances combined.
    val strings = instances.map(_.asStrings).transpose.map(_.flatten)

    for ((filename, lines) <- filenames.zip(strings)) {
      val indexed = if (withIndices) {
        lines.zipWithIndex.map { case (string, index) => s"$index\t$string" }
      } else {
        lines
      }
      fileUtil.writeLinesToFile(filename, indexed)
    }
  }
}

trait DatasetReader[T <: Instance] {
  def readFile(filename: String): Dataset[T]
}

object DatasetReader {
  implicit val formats = DefaultFormats
  val readers = new mutable.HashMap[String, (JValue, FileUtil) => DatasetReader[_]]
  readers.put("babi", (params, fileUtil) => new BabiDatasetReader(fileUtil))
  readers.put("children's books", (params, fileUtil) => new ChildrensBookDatasetReader(fileUtil))
  readers.put("snli", (params, fileUtil) => new SnliDatasetReader(fileUtil))
  readers.put("open qa", (params, fileUtil) => new OpenQADatasetReader(fileUtil))
  readers.put("squad", (params, fileUtil) => new SquadDatasetReader(fileUtil))
  readers.put("squad tagging", (params, fileUtil) => new SquadTaggingDatasetReader(params))
  readers.put("who did what", (params, fileUtil) => new WhoDidWhatDatasetReader(fileUtil))
  readers.put("newsqa", (params, fileUtil) => new NewsQaDatasetReader(fileUtil))
  readers.put("sciq", (params, fileUtil) => new SciQDatasetReader(fileUtil))
  readers.put("omnibus da", (params, fileUtil) => new OmnibusDaDatasetReader())

  def create(params: JValue, fileUtil: FileUtil): DatasetReader[_] = {
    params match {
      case JString(readerType) => readers(readerType)(JNothing, fileUtil)
      case jval => {
        val readerType = (jval \ "type").extract[String]
        readers(readerType)(jval, fileUtil)
      }
    }
  }
}
