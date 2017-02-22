package org.allenai.deep_qa.data

import com.mattg.util.FileUtil
import org.scalatest._

class OmnibusDaDatasetReaderSpec extends FlatSpecLike with Matchers {

  val fileUtil = new FileUtil
  val questionText1 = "Coal is a nonrenewable energy resource. Identify the original "+
    "source of the energy stored in coal."
  val answerText1 = "The Sun is the original source of energy."
  val header = """"id","parentId","isArchived","questionText",""" +
    """"answerText","points","isTest","isMultipleChoice","hasDiagram",""" +
    """"examName","examGrade","examYear","notes","tags","legacyId",""" +
    """importedQuestionId"""

  val row1 = """"1",,"false","""" + s"${questionText1}" + """","""" + s"${answerText1}" + """",""" +
    """"1","false","false","false","Source1","5","2015","","Tag1",""" +
    """"legacyId1","importedId1""""

  val questionText2 = "Coal is a nonrenewable energy resource. People burn coal to " +
    "make electricity and heat buildings. Explain how long it would most likely take for new coal " +
    "to form."
  val answerText2 = "Coal may slowly form over millions of years."
  val row2 = """"1",,"false","""" + s"${questionText2}"+"""","""" + s"${answerText2}" + """",""" +
    """"1","false","false","false","Source2","5","2015","","Tag2",""" +
    """"legacyId2","importedId2""""


  val datasetFile = "./dataset"
  val datasetFileContents = s"""${header}
      |${row1}
      |${row2}""".stripMargin

  val reader = new OmnibusDaDatasetReader()
  "readFile" should "return a correct dataset" in {
    fileUtil.mkdirsForFile(datasetFile)
    fileUtil.writeContentsToFile(datasetFile, datasetFileContents)
    val dataset = reader.readFile(datasetFile)
    fileUtil.deleteFile(datasetFile)

    dataset.instances.size should be(2)
    dataset.instances(0) should be(DirectAnswerInstance(questionText1, Some(answerText1)))
    dataset.instances(1) should be(DirectAnswerInstance(questionText2, Some(answerText2)))
  }
}
