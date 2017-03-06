package org.allenai.deep_qa.data

import com.mattg.util.FileUtil
import org.scalatest._

class OmnibusDaDatasetReaderSpec extends FlatSpecLike with Matchers {

  val fileUtil = new FileUtil
  val questionText1 = "Coal is a nonrenewable energy resource. Identify the original " +
    "source of the energy stored in coal."
  val transformedQuestionText1 = "Coal is a nonrenewable energy resource. What is the " +
  "original source of the energy stored in coal?"
  val answerText1 = "The Sun is the original source of energy."
  val header = """"id","parentId","isArchived","questionText",""" +
    """"answerText","points","isTest","isMultipleChoice","hasDiagram",""" +
    """"examName","examGrade","examYear","notes","tags","legacyId",""" +
    """importedQuestionId"""
  val row1 = s""""1",,"false","${questionText1}","${answerText1}",""" +
    """"1","false","false","false","Source1","5","2015","","Tag1",""" +
    """"legacyId1","importedId1""""

  val questionText2 = "Coal is a nonrenewable energy resource. People burn coal to " +
    "make electricity and heat buildings. Explain how long it would most likely take for new coal " +
    "to form."
  val transformedQuestionText2 = "Coal is a nonrenewable energy resource. People burn coal to " +
    "make electricity and heat buildings. How long it would most likely take for new coal to form?"
  val answerText2 = "Coal may slowly form over millions of years."
  val row2 = s""""1",,"false","${questionText2}","${answerText2}",""" +
    """"1","false","false","false","Source2","5","2015","","Tag2",""" +
    """"legacyId2","importedId2""""

  val questionText3 = "Read the given statement and determine if the behavior is "+
    "either learned or innate. A bear eating salmon caught from a river."
  val transformedQuestionText3 = "Read the given statement and determine if the "+
    "behavior is either learned or innate. A bear eating salmon caught from a river."

  val questionText4 = "Even though an adult amphibian can live on land, why must it " +
    "return to the water? (Explain.)"
  // TODO(nelson): Fix this to preserve case.
  val transformedQuestionText4 = "Even though an adult amphibian can live on land, " +
    "why must it return to the water? (explain.)"

  val questionText5 = "Name four organisms you might find living in or around a " +
    "freshwater lake."
  val transformedQuestionText5 = "What are four organisms you might find living in or around a " +
  "freshwater lake?"
  val questionText6 = "List the conditions that must be present in order for a " +
  "seed to germinate."
  val transformedQuestionText6 = "What are the conditions that must be present in "+
  "order for a seed to germinate?"

  // TODO(nelson): not clear how to insert the proper verbs here.
  val questionText7 = "Briefly explain why most animal life on Earth could not survive without plants."
  val transformedQuestionText7 = "Why most animal life on earth could not survive without plants?"

  val questionText8 = "The earthworm has no lungs and breathes in oxygen through " +
    "its moist skin. If its skin dries out, the worm will suffocate. " +
    "Mucus-producing cells also cover its skin. Not only does the mucus help keep "+
    "the body moist, but it also makes the worm's body slippery to help it move "+
    "through the soil. In addition, the worm's mucus-covered skin helps prevent "+
    "collapsing of the tunnel walls by packing the soil particles together. In your "+
    "own words, explain how the earthworms described in the reading passage breathe."
  val transformedQuestionText8 = "The earthworm has no lungs and breathes in oxygen "+
    "through its moist skin. If its skin dries out, the worm will suffocate. " +
    "Mucus-producing cells also cover its skin. Not only does the mucus help keep "+
    "the body moist, but it also makes the worm's body slippery to help it move "+
    "through the soil. In addition, the worm's mucus-covered skin helps prevent "+
    "collapsing of the tunnel walls by packing the soil particles together. "+
    "How the earthworms described in the reading passage breathe?"

  val questionText9 = "Explain how the sun provides energy to ecosystems."
  val transformedQuestionText9 = "How the sun provides energy to ecosystems?"

  // Test that non-greedy imperative match works as expected.
  val questionText10 = "Name two examples of habitats, and one organism that would live in " +
    "each habitat."
  val transformedQuestionText10 = "What are two examples of habitats, and one organism that would " +
    "live in each habitat?"

  val questionText11 = "What is another name for the \"bristles\" " +
    "mentioned in the reading passage?"

  val transformedQuestionText11 = "What is another name for the " +
    "\"bristles\" mentioned in the reading passage?"

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
    dataset.instances(0) should be(DirectAnswerInstance(transformedQuestionText1, Some(answerText1)))
    dataset.instances(1) should be(DirectAnswerInstance(transformedQuestionText2, Some(answerText2)))
  }
  "imperativeToInterrogative" should "return an imperative question converted to interrogative" in {
    reader.imperativeToInterrogative(questionText1) should be (transformedQuestionText1)
    reader.imperativeToInterrogative(questionText2) should be (transformedQuestionText2)
    reader.imperativeToInterrogative(questionText3) should be (transformedQuestionText3)
    reader.imperativeToInterrogative(questionText4) should be (transformedQuestionText4)
    reader.imperativeToInterrogative(questionText5) should be (transformedQuestionText5)
    reader.imperativeToInterrogative(questionText6) should be (transformedQuestionText6)
    reader.imperativeToInterrogative(questionText7) should be (transformedQuestionText7)
    reader.imperativeToInterrogative(questionText8) should be (transformedQuestionText8)
    reader.imperativeToInterrogative(questionText9) should be (transformedQuestionText9)
    reader.imperativeToInterrogative(questionText10) should be (transformedQuestionText10)
    reader.imperativeToInterrogative(questionText11) should be (transformedQuestionText11)
  }
}
