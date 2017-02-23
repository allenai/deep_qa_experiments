package org.allenai.deep_qa.experiments.datasets

import org.json4s._
import org.json4s.JsonDSL._

/**
 * This object contains a bunch of JValue specifications for the
 * Omnibus DA dataset's data files.
 */
object OmnibusDa {

  def omnibusDaFile(omnibusDaDir: String, grade: String, split: String): JValue = {
    val grade_str = if (grade.length == 1) {
      "0" + grade
    }
    else{
      grade
    }
    val datasetGradePath = s"Omnibus-Gr${grade_str}-NDDA"
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

  /////////////////////////////////////////////////////////////////////
  // Omnibus4 NDDA
  /////////////////////////////////////////////////////////////////////

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

  /////////////////////////////////////////////////////////////////////
  // Omnibus8 NDDA
  /////////////////////////////////////////////////////////////////////
  // Train files
  val omnibusNdda8TrainFile = omnibusDaFile(baseDir, "8", "train")
  val omnibusNdda8Train = omnibusDaDataset(baseDir, "8", "train")
  val omnibusNdda8TrainBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNdda8TrainFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNdda8TrainFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNdda8TrainFile) ~
    ("background" -> omnibusNdda8TrainBuscBackgroundFile)

  // Dev Files
  val omnibusNdda8DevFile = omnibusDaFile(baseDir, "8", "dev")
  val omnibusNdda8Dev = omnibusDaDataset(baseDir, "8", "dev")
  val omnibusNdda8DevBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNdda8DevFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNdda8DevFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNdda8DevFile) ~
    ("background" -> omnibusNdda8DevBuscBackgroundFile)

  // Test Files
  val omnibusNdda8TestFile = omnibusDaFile(baseDir, "8", "test")
  val omnibusNdda8Test = omnibusDaDataset(baseDir, "8", "test")
  val omnibusNdda8TestBuscBackgroundFile = SciQDatasets.makePassageBackgroundFile(
    omnibusNdda8TestFile,
    "question and answer",
    ScienceCorpora.buscElasticSearchIndex(3)
  )
  val omnibusNdda8TestFileWithBackground: JValue =
    ("sentence producer type" -> "combine background and instance") ~
    ("sentences" -> omnibusNdda8TestFile) ~
    ("background" -> omnibusNdda8TestBuscBackgroundFile)
}
