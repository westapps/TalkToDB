package com.westapps.talktodb.aws

import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import software.amazon.awssdk.services.sqs.model.PurgeQueueResponse
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse
import software.amazon.awssdk.services.sqs.model.{SendMessageRequest, SendMessageResponse}

class ReactiveSqsClient(
  private val sqsAsyncClient: SqsAsyncClient
) {
  def sendMessage(sendMessageRequest: SendMessageRequest): Mono[SendMessageResponse] = {
    Mono.fromFuture(sqsAsyncClient.sendMessage(sendMessageRequest))
  }

  def sendMessageBatch(request: SendMessageBatchRequest): Mono[SendMessageBatchResponse] = {
    Mono.fromFuture(sqsAsyncClient.sendMessageBatch(request))
  }

  def receiveMessage(request: ReceiveMessageRequest): Mono[ReceiveMessageResponse] = {
    Mono.fromFuture(sqsAsyncClient.receiveMessage(request))
  }

  def deleteMessage(request: DeleteMessageRequest): Mono[DeleteMessageResponse] = {
    Mono.fromFuture(sqsAsyncClient.deleteMessage(request))
  }

  def purgeQueue(request: PurgeQueueRequest): Mono[PurgeQueueResponse] = {
    Mono.fromFuture(sqsAsyncClient.purgeQueue(request))
  }
}
