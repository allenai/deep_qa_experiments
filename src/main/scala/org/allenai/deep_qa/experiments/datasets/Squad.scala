package org.allenai.deep_qa.experiments.datasets

import org.json4s._
import org.json4s.JsonDSL._

/**
 * This object contains a bunch of JValue specifications for SQuAD data files.
 */
object SquadDatasets {

  def squadFile(squadDir: String, split: String, version: String="1.1"): JValue = {
    val outputDirectory = squadDir + "processed/"
    val inputFile = squadDir + s"${split}-v${version}.json"
    val outputFiles = Seq(outputDirectory + s"${split}.tsv")
    ("sentence producer type" -> "dataset reader") ~
    ("reader" -> "squad") ~
    ("input file" -> inputFile) ~
    ("output files" -> outputFiles)
  }

  def squadDataset(squadDir: String, split: String, version: String="1.1"): JValue = {
    val file = squadFile(squadDir, split, version)
    ("data files" -> List(file))
  }

  def squadTaggingFile(
    squadDir: String,
    split: String,
    version: String="1.1",
    tokenize: Boolean=true
  ): JValue = {
    val outputDirectory = squadDir + "processed/"
    val inputFile = squadDir + s"${split}-v${version}.json"
    val outputFiles = Seq(outputDirectory + s"tagging_${split}.tsv")
    ("sentence producer type" -> "dataset reader") ~
      ("reader" -> ("type" -> "squad tagging") ~ ("tokenize" -> tokenize)) ~
      ("input file" -> inputFile) ~
      ("output files" -> outputFiles)
  }

  def squadTaggingDataset(
    squadDir: String,
    split: String,
    version: String="1.1",
    tokenize: Boolean=true
  ): JValue = {
    val file = squadTaggingFile(squadDir, split, version, tokenize)
    ("data files" -> List(file))
  }

  val baseDir = "/efs/data/dlfa/squad/"

  val trainFile = squadFile(baseDir, "train")
  val trainDataset = squadDataset(baseDir, "train")
  val devFile = squadFile(baseDir, "dev")
  val devDataset = squadDataset(baseDir, "dev")

  val taggingTrainFile = squadTaggingFile(baseDir, "train")
  val taggingTrainDataset = squadTaggingDataset(baseDir, "train")
  val taggingDevFile = squadTaggingFile(baseDir, "dev")
  val taggingDevDataset = squadTaggingDataset(baseDir, "dev")
}
