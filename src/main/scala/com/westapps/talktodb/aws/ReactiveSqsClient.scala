package com.westapps.talktodb.aws

import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.{SendMessageRequest, SendMessageResponse}

class ReactiveSqsClient(
  private val client: SqsAsyncClient
) {
  def sendMessage(sendMessageRequest: SendMessageRequest): Mono[SendMessageResponse] = {
    Mono.fromFuture(client.sendMessage(sendMessageRequest))
  }
}
