package org.allenai.deep_qa.experiments.datasets

import org.json4s._
import org.json4s.JsonDSL._

/**
 * This object contains a bunch of JValue specifications for the
 * Omnibus DA dataset's data files.
 */
object OmnibusDa {

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
  val omnibusNdda4TrainFile = omnibusDaFile(baseDir, "4", "train")
  val omnibusNdda4Train = omnibusDaDataset(baseDir, "4", "train")
  val omnibusNdda4TrainBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNdda4TrainFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNdda4TrainFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNdda4TrainFile) ~
    ("background" -> omnibusNdda4TrainBuscBackgroundFile)

  // Dev Files
  val omnibusNdda4DevFile = omnibusDaFile(baseDir, "4", "dev")
  val omnibusNdda4Dev = omnibusDaDataset(baseDir, "4", "dev")
  val omnibusNdda4DevBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNdda4DevFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNdda4DevFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNdda4DevFile) ~
    ("background" -> omnibusNdda4DevBuscBackgroundFile)

  // Test Files
  val omnibusNdda4TestFile = omnibusDaFile(baseDir, "4", "test")
  val omnibusNdda4Test = omnibusDaDataset(baseDir, "4", "test")
  val omnibusNdda4TestBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNdda4TestFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNdda4TestFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNdda4TestFile) ~
    ("background" -> omnibusNdda4TestBuscBackgroundFile)
}
