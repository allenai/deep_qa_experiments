package org.allenai.deep_qa.experiments.datasets

import org.json4s._
import org.json4s.JsonDSL._

/**
 * This object contains a bunch of JValue specifications for the
 * Omnibus DA dataset's data files.
 */
object omnibusDa {

  def omnibusDaFile(omnibusDaDir: String, grade: String, split: String): JValue = {
    if (grade.length == 1) {
      val grade_str = "0" + grade
    }
    else{
      val grade_str = grade
    }
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
  val omnibusNDDA4TrainFile = omnibusDaFile(baseDir, "4", "train")
  val omnibusNDDA4Train = omnibusDaDataset(baseDir, "4", "train")
  val omnibusNDDA4TrainBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNDDA4TrainFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNDDA4TrainFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNDDA4TrainFile) ~
    ("background" -> omnibusNDDA4TrainBuscBackgroundFile)

  // Dev Files
  val omnibusNDDA4DevFile = omnibusDaFile(baseDir, "4", "dev")
  val omnibusNDDA4Dev = omnibusDaDataset(baseDir, "4", "dev")
  val omnibusNDDA4DevBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNDDA4DevFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNDDA4DevFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNDDA4DevFile) ~
    ("background" -> omnibusNDDA4DevBuscBackgroundFile)

  // Test Files
  val omnibusNDDA4TestFile = omnibusDaFile(baseDir, "4", "test")
  val omnibusNDDA4Test = omnibusDaDataset(baseDir, "4", "test")
  val omnibusNDDA4TestBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNDDA4TestFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNDDA4TestFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNDDA4TestFile) ~
    ("background" -> omnibusNDDA4TestBuscBackgroundFile)
}
