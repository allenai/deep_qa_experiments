package org.allenai.deep_qa.solver

import scala.sys.process.Process

import org.allenai.deep_qa.data.Instance
import org.allenai.deep_qa.data.BackgroundInstance
import org.allenai.deep_qa.data.CharacterSpanInstance
import org.allenai.deep_qa.data.QuestionAnswerInstance
import org.allenai.deep_qa.data.MultipleTrueFalseInstance
import org.allenai.deep_qa.data.TrueFalseInstance
import org.allenai.deep_qa.message.SolverServiceGrpc.SolverServiceBlockingStub
import org.allenai.deep_qa.message.{Instance => MessageInstance}
import org.allenai.deep_qa.message.InstanceType
import org.allenai.deep_qa.message.SolverServiceGrpc
import org.allenai.deep_qa.message.QuestionRequest
import org.allenai.deep_qa.message.QuestionResponse

import com.mattg.util.FileUtil
import com.typesafe.scalalogging.LazyLogging

import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder

import java.io.File
import java.util.concurrent.TimeUnit

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{parse,pretty,render}

/**
 * This Client connects to a python server that returns predictions from a trained model.  The only
 * thing we do here is convert between an Instance object and an Instance proto, then send the
 * proto off to the server, returning the scores we get back.
 */
class Client(host: String, port: Int) extends LazyLogging {
  val fileUtil = new FileUtil
  val channel = OkHttpChannelBuilder.forAddress(host, port).usePlaintext(true).build
  val blockingStub = SolverServiceGrpc.blockingStub(channel)

  def shutdown(): Unit = {
    logger.info("Closing connection to the server")
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  def answerQuestion(instance: Instance): Seq[Double] = {
    val response = sendMessage(instanceToMessage(instance))
    response.scores
  }

  def instanceToMessage(instance: Instance): MessageInstance = {
    instance match {
      case i: BackgroundInstance[_] => {
        val containedMessage = instanceToMessage(i.containedInstance)
        val instanceType = containedMessage.`type`
        val questionText = containedMessage.question
        val answerOptions = containedMessage.answerOptions
        val background = i.background
        val backgroundInstances = background.map(sentence => {
          MessageInstance(
            InstanceType.TRUE_FALSE,
            sentence,
            Seq(),
            Seq(),
            Seq()
          )
        })
        MessageInstance(instanceType, questionText, answerOptions, backgroundInstances)
      }
      case i: QuestionAnswerInstance => {
        val instanceType = InstanceType.QUESTION_ANSWER
        val questionText = i.question
        val answerOptions = i.answers
        MessageInstance(instanceType, questionText, answerOptions, Seq(), Seq(), "")
      }
      case i: MultipleTrueFalseInstance[_] => {
        val instanceType = InstanceType.MULTIPLE_TRUE_FALSE
        val containedInstances = i.instances.map(instanceToMessage)
        MessageInstance(instanceType, "", Seq(), Seq(), containedInstances, "")
      }
      case i: TrueFalseInstance => {
        val instanceType = InstanceType.TRUE_FALSE
        val questionText = i.statement
        val answerOptions = Seq()
        MessageInstance(instanceType, questionText, answerOptions, Seq(), Seq(), "")
      }
      case i: CharacterSpanInstance => {
        val instanceType = InstanceType.CHARACTER_SPAN
        val questionText = i.question
        val passageText = i.passage
        MessageInstance(instanceType, questionText, Seq(), Seq(), Seq(), passageText)
      }
    }
  }

  def sendMessage(message: MessageInstance): QuestionResponse = {
    val request: QuestionRequest = QuestionRequest(Some(message))
    blockingStub.answerQuestion(request)
  }
}

/**
 * This is just for testing things, and showing how to instantiate a Client object and have it
 * answer questions.
 */
object Client {

  def main(args: Array[String]) {
    val client: Client = new Client("localhost", 50051)

    val instance = MultipleTrueFalseInstance(Seq(
      BackgroundInstance(TrueFalseInstance("statement 1", None), Seq("background 1")),
      BackgroundInstance(TrueFalseInstance("statement 2", None), Seq("background 2")),
      BackgroundInstance(TrueFalseInstance("statement 3", None), Seq("background 3")),
      BackgroundInstance(TrueFalseInstance("statement 4", None), Seq("background 4"))
    ), None)

    val scores = client.answerQuestion(instance)
    println(s"Scores: [${scores.mkString(" ")}]")
    client.shutdown()
  }
}
