package org.allenai.deep_qa.experiments.datasets

import org.json4s._
import org.json4s.JsonDSL._

/**
 * This object contains a bunch of JValue specifications for the
 * Omnibus DA dataset's data files.
 */
object omnibusDaDatasets {

  def omnibusDaFile(omnibusDaDir: String, grade: String, split: String): JValue = {
    val datasetGradePath = s"Omnibus-Gr${grade}-NDDA"
    val outputDirectory = omnibusDaDir + datasetGradePath +s"/processed/${split}/"
    val splitCapitalized = split.capitalize
    val inputFile = omnibusDaDir + datasetGradePath + s"/${datasetGradePath}-${splitCapitalized}.csv"
    val outputFiles = Seq(outputDirectory + s"${split}.tsv")
    ("sentence producer type" -> "dataset reader") ~
      ("reader" -> "omnibus da") ~
      ("create sentence indices" -> true) ~
      ("input file" -> inputFile) ~
      ("output files" -> outputFiles)
  }

  def omnibusDaDataset(omnibusDaDir: String, grade: String, split: String): JValue = {
    val file = omnibusDaFile(omnibusDaDir, grade, split)
    ("data files" -> List(file))
  }

  val baseDir = "/efs/data/dlfa/omnibus_ndda/"
  // Train files
  val omnibusNDDA04TrainFile = omnibusDaFile(baseDir, "04", "train")
  val omnibusNDDA04Train = omnibusDaDataset(baseDir, "04", "train")
  val omnibusNDDA04TrainBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNDDA04TrainFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )

  // Dev Files
  val omnibusNDDA04DevFile = omnibusDaFile(baseDir, "04", "dev")
  val omnibusNDDA04Dev = omnibusDaDataset(baseDir, "04", "dev")
  val omnibusNDDA04DevBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNDDA04DevFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )

  // Test Files
  val omnibusNDDA04TestFile = omnibusDaFile(baseDir, "04", "test")
  val omnibusNDDA04Test = omnibusDaDataset(baseDir, "04", "test")
  val omnibusNDDA04TestBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNDDA04TestFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
}
