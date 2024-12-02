package com.westapps.talktodb.aws

import com.fasterxml.jackson.databind.ObjectMapper
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.model.{SendMessageRequest, SendMessageResponse}

class SqsSender(
	sqsUrl: String,
	client: ReactiveSqsClient,
	objectMapper: ObjectMapper
) {
	def send[T](t: T): Mono[SendMessageResponse] = {
		val message = objectMapper.writeValueAsString(t)
		val request = SendMessageRequest.builder
			.queueUrl(sqsUrl)
			.messageBody(message)
			.build()
		client.sendMessage(request)
	}
}
