package com.westapps.talktodb.aws

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.LazyLogging
import com.westapps.talktodb.aws.SqsClient.JsonMessage
import com.westapps.talktodb.aws.SqsClient.SQSResponse
import com.westapps.talktodb.aws.SqsClient.SQSSentFailure
import com.westapps.talktodb.aws.SqsClient.SQSSentSuccess
import reactor.core.publisher.Mono
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

import java.time.LocalDateTime
import java.util.UUID
import scala.jdk.CollectionConverters._
import scala.concurrent.ExecutionContext

class SqsClient(
  private val reactiveSqsClient: ReactiveSqsClient,
  private val queueArn: String
)(implicit private val executionContext: ExecutionContext) extends LazyLogging {
  private val retrieveMessageWaitTime = 20 // based on AWS SQS document: Must be >= 0 and <= 20 seconds
  private val visibilityTimeout = 60
  private val queueArnComps: Array[String] = queueArn.split(":") // [arn, aws, sqs, region, account, queue]
  private val queueUrl = "https://sqs." + queueArnComps(3) + ".amazonaws.com/" + queueArnComps(4) + "/" + queueArnComps(5)

  def send(msgObj: JsonMessage): Mono[SQSResponse] = {
    val sendMessageRequest = SendMessageRequest
      .builder()
      .queueUrl(queueUrl)
      .messageBody(msgObj.strVal())
      .build()

    reactiveSqsClient.sendMessage(sendMessageRequest).map(resp => handleSdkHttpResponse(resp.sdkHttpResponse()))
  }

  def sendBatch(messages: Seq[JsonMessage]): Mono[SQSResponse] = {
    val entries = messages.map { msg =>
      SendMessageBatchRequestEntry
        .builder()
        .id(UUID.randomUUID().toString)
        .messageBody(msg.strVal())
        .build()
    }

    val request = SendMessageBatchRequest
      .builder()
      .queueUrl(queueUrl)
      .entries(entries.asJava)
      .build()

    reactiveSqsClient.sendMessageBatch(request).map(resp => handleSdkHttpResponse(resp.sdkHttpResponse()))
  }

  def retrieve(): Mono[Option[Message]] = {
    val receiveMessageRequest = ReceiveMessageRequest
      .builder()
      .queueUrl(queueUrl)
      .maxNumberOfMessages(1)
      .visibilityTimeout(visibilityTimeout)
      .waitTimeSeconds(retrieveMessageWaitTime)
      .build()

    reactiveSqsClient.receiveMessage(receiveMessageRequest).map { sqsResponse =>
      logger.info(s"read from $queueUrl at ${LocalDateTime.now}")
      if (sqsResponse.sdkHttpResponse().isSuccessful && !sqsResponse.messages().isEmpty) {
        Some(sqsResponse.messages().iterator().asScala.toSeq.head)
      } else {
        None
      }
    }
  }

  def delete(message: Message): Mono[SQSResponse] = {
    val deleteMessageRequest = DeleteMessageRequest
      .builder()
      .queueUrl(queueUrl)
      .receiptHandle(message.receiptHandle())
      .build()

    reactiveSqsClient.deleteMessage(deleteMessageRequest).map(resp => handleSdkHttpResponse(resp.sdkHttpResponse()))
  }

  def purge(): Mono[SQSResponse] = {
    val purgeQueueRequest = PurgeQueueRequest
      .builder()
      .queueUrl(queueUrl)
      .build()

    reactiveSqsClient.purgeQueue(purgeQueueRequest).map(resp => handleSdkHttpResponse(resp.sdkHttpResponse()))
  }

  private def handleSdkHttpResponse(resp: SdkHttpResponse): SQSResponse = {
    if (resp.isSuccessful) {
      SQSSentSuccess
    } else {
      SQSSentFailure
    }
  }
}

object SqsClient {
  trait SQSResponse {
    val message: String
  }

  case object SQSSentSuccess extends SQSResponse {
    val message: String = "SQS Message Sent Successfully"
  }

  case object SQSSentFailure extends SQSResponse {
    val message: String = "SQS Message Sent Failed"
  }

  case class JsonMessage(body: JsonNode) {
    def strVal(): String = {
      val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
      mapper.writeValueAsString(body)
    }
  }
}
