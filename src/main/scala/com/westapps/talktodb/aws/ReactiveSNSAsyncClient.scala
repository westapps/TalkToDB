package com.westapps.talktodb.aws

import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.PublishResponse

class ReactiveSNSAsyncClient(
  private val snsAsyncClient: SnsAsyncClient
) {
  def publish(publishRequest: PublishRequest): Mono[PublishResponse] = {
    Mono.fromFuture(snsAsyncClient.publish(publishRequest))
  }
}
