package org.allenai.deep_qa.experiments

import org.allenai.deep_qa.experiments.datasets.SciQDatasets
import org.allenai.deep_qa.experiments.datasets.WhoDidWhatDatasets

import com.mattg.util.FileUtil
import org.json4s.JsonDSL._
import org.json4s._

object TransferExperiments {
  val fileUtil = new FileUtil

  def sciQTrainWithTruncatedWhoDidWhatTrain(linesToKeep: Int): JValue = {
    val truncatedQuestions: JValue =
      ("dataset type" -> "truncated") ~
        ("dataset to truncate" -> WhoDidWhatDatasets.train) ~
        ("instances to keep" -> linesToKeep) ~
        ("output directory" -> s"/efs/data/dlfa/who_did_what/processed/truncated/train/")

    val combinedDataset: JValue =
      ("dataset type" -> "combined") ~
        ("datasets" -> Seq(
          truncatedQuestions,
          SciQDatasets.sciQTrainDataset)) ~
      ("output directory" -> s"/efs/data/dlfa/sciq/sciq_and_who_did_what_combined/train/")
    combinedDataset
  }
}
