package com.westapps.talktodb.aws

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import software.amazon.awssdk.services.dynamodb.model.QueryResponse
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse

@Component
class ReactiveDynamoDbClient(
  @Autowired val dynamoDbAsyncClient: DynamoDbAsyncClient
) {
  def getItem(getItemRequest: GetItemRequest): Mono[GetItemResponse] = {
    Mono.fromFuture(dynamoDbAsyncClient.getItem(getItemRequest))
  }

  def putItem(putItemRequest: PutItemRequest): Mono[PutItemResponse] = {
    Mono.fromFuture(dynamoDbAsyncClient.putItem(putItemRequest))
  }

  def deleteItem(deleteItemRequest: DeleteItemRequest): Mono[DeleteItemResponse] = {
    Mono.fromFuture(dynamoDbAsyncClient.deleteItem(deleteItemRequest))
  }

  def batchGetItem(batchGetItemRequest: BatchGetItemRequest): Mono[BatchGetItemResponse] = {
    Mono.fromFuture(dynamoDbAsyncClient.batchGetItem(batchGetItemRequest))
  }

  def query(queryRequest: QueryRequest): Mono[QueryResponse] = {
    Mono.fromFuture(dynamoDbAsyncClient.query(queryRequest))
  }

  def updateItem(updateItemRequest: UpdateItemRequest): Mono[UpdateItemResponse] = {
    Mono.fromFuture(dynamoDbAsyncClient.updateItem(updateItemRequest))
  }
}
