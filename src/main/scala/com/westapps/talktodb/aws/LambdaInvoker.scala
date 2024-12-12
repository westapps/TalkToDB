package com.westapps.talktodb.aws

import com.typesafe.scalalogging.LazyLogging
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaAsyncClient
import software.amazon.awssdk.services.lambda.model.InvocationType
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse

import scala.collection.convert.AsScalaExtensions

class LambdaInvoker(
  private val reactiveLambdaClient: ReactiveLambdaClient,
  private val lambdaFunctionName: String
) extends AsScalaExtensions with LazyLogging {
  def runWithPayload(payload: String): Mono[InvokeResponse] = {
    val invokeRequest: InvokeRequest = InvokeRequest
      .builder()
      .functionName(lambdaFunctionName)
      .payload(SdkBytes.fromUtf8String(payload))
      .invocationType(InvocationType.REQUEST_RESPONSE)
      .build()

    reactiveLambdaClient.invoke(invokeRequest)
  }
}

class ReactiveLambdaClient(
  private val lambdaAsyncClient: LambdaAsyncClient
) {
  def invoke(invokeRequest: InvokeRequest): Mono[InvokeResponse] = {
    Mono.fromFuture(lambdaAsyncClient.invoke(invokeRequest))
  }
}

