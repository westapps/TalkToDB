package com.westapps.talktodb.aws

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.westapps.talktodb.aws.SNSClient.SNSResponse
import com.westapps.talktodb.aws.SNSClient.SNSSentFailure
import com.westapps.talktodb.aws.SNSClient.SNSSentSuccess
import com.westapps.talktodb.aws.SqsClient.JsonMessage
import reactor.core.publisher.Mono
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.PublishResponse

class SNSClient(
  private val reactiveSNSAsyncClient: ReactiveSNSAsyncClient,
  private val snsTopicArn: Option[String] = None
) {
  def publish(msg: String): Mono[PublishResponse] = {
    val publishRequest = PublishRequest
      .builder()
      .topicArn(snsTopicArn.getOrElse(""))
      .message(msg)
      .build()

    reactiveSNSAsyncClient.publish(publishRequest)
  }

  def publish(msgObj: JsonMessage): Mono[SNSResponse] = {
    val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
    val msg: String = mapper.writeValueAsString(msgObj.body)
    publish(msg).map{resp => handleSdkHttpResponse(resp.sdkHttpResponse())}
  }

  private def handleSdkHttpResponse(resp: SdkHttpResponse): SNSResponse = {
    if (resp.isSuccessful) {
      SNSSentSuccess
    } else {
      SNSSentFailure
    }
  }
}

object SNSClient{
  trait SNSResponse {
    val message: String
  }

  case object SNSSentSuccess extends SNSResponse {
    val message: String = "SNSClient Message Sent Successfully"
  }

  case object SNSSentFailure extends SNSResponse {
    val message: String = "SNSClient Message Sent Failed"
  }
}
