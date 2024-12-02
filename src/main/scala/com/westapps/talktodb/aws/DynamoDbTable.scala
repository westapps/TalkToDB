package com.westapps.talktodb.aws

import com.westapps.talktodb.aws.DynamoDbTable.CommonQueryRequest
import com.westapps.talktodb.aws.DynamoDbTable.ConditionEntry
import com.westapps.talktodb.aws.DynamoDbTable.UpdateContext
import com.westapps.talktodb.exceptions.ApplicationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator
import software.amazon.awssdk.services.dynamodb.model.Condition
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import software.amazon.awssdk.services.dynamodb.model.ReturnValue
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest

import scala.collection.convert.AsJavaExtensions
import scala.collection.convert.AsScalaExtensions

class DynamoDbTable[T](
  private val reactiveDynamoDbClient: ReactiveDynamoDbClient,
  private val tableName: String,
  private val serializer: DynamoDbSerializer[T]
) extends AsJavaExtensions with AsScalaExtensions {
  private val serializerImpl = new DynamoDbSerializerAdapter[T](serializer)

  def get(key: String, sortKey: Option[String]): Mono[T] = {
    val request: GetItemRequest = GetItemRequest.builder()
      .key(serializerImpl.toKey(key, sortKey))
      .tableName(tableName)
      .build()

    reactiveDynamoDbClient.getItem(request).map { response: GetItemResponse =>
      if (response.hasItem) {
        serializerImpl.deserializeItem(response.item())
      } else {
        throw new ApplicationException("item is not found.")
      }
    }
  }

  def putItem(item: T): Mono[PutItemResponse] = {
    val putItemRequest = PutItemRequest.builder()
      .tableName(tableName)
      .item(serializerImpl.serializeItem(item).asJava)
      .build()

    reactiveDynamoDbClient.putItem(putItemRequest).map{ putItemResponse: PutItemResponse =>
      if (putItemResponse.sdkHttpResponse().isSuccessful) {
        putItemResponse
      } else {
        throw new ApplicationException("put item failed.")
      }
    }
  }

  def deleteItem(primaryKey: Any, sortKey: Option[Any]): Mono[Any] = {
    val deleteItemRequest: DeleteItemRequest = DeleteItemRequest.builder()
      .tableName(tableName)
      .key(serializerImpl.toKey(primaryKey, sortKey))
      .build()

    reactiveDynamoDbClient.deleteItem(deleteItemRequest).map{ deleteItemResponse: DeleteItemResponse =>
      if (deleteItemResponse.sdkHttpResponse().isSuccessful) {
        primaryKey
      } else {
        throw new ApplicationException("delete item failed.")
      }
    }
  }

    def updateItem(
    props: Map[String, Any],
    primaryKey: Any,
    sortKey: Option[Any]
  ): Mono[Option[T]] = {
      val updateContext = UpdateContext.builder(props, sortKey, serializer).build()
      val updateItemRequestBuilder: UpdateItemRequest.Builder = UpdateItemRequest.builder()
        .tableName(tableName)
        .key(serializerImpl.toKey(primaryKey, sortKey))
        .updateExpression(updateContext.updateExpression)
        .conditionExpression(updateContext.conditionExpression)
        .expressionAttributeNames(updateContext.expressionAttributeNames.asJava)
        .returnValues(ReturnValue.ALL_NEW)

      if (updateContext.expressionAttributeValues.nonEmpty) {
        updateItemRequestBuilder.expressionAttributeValues(updateContext.expressionAttributeValues.asJava)
      }
      val updateItemRequest = updateItemRequestBuilder.build()

    reactiveDynamoDbClient.updateItem(updateItemRequest).map { updateItemResponse =>
        if (updateItemResponse.sdkHttpResponse().isSuccessful) {
          Some(serializerImpl.deserializeItem(updateItemResponse.attributes()))
        } else {
          throw new ApplicationException("Item update failed.")
        }
      }
  }

  def find(key: String, sortKey: Option[String]): Mono[Option[T]] = {
    val request: GetItemRequest = GetItemRequest.builder()
      .key(serializerImpl.toKey(key, sortKey))
      .tableName(tableName)
      .build()

    reactiveDynamoDbClient.getItem(request).map { response: GetItemResponse =>
      if (response.hasItem) {
        Some(serializerImpl.deserializeItem(response.item()))
      } else {
        None
      }
    }
  }

  def query(
    keyConditionEntries: Seq[ConditionEntry],
    queryFilterEntries: Seq[ConditionEntry],
    orderAscending: Boolean,
    size: Option[Int],
    indexName: Option[String]
  ): Flux[T] = {
    val request: QueryRequest = CommonQueryRequest
      .builder(keyConditionEntries, queryFilterEntries, orderAscending, size, indexName, tableName)
      .build()

    val result = reactiveDynamoDbClient.query(request).map { item =>
      serializerImpl.deserializeItems(item.items())
    }

    result.flatMapMany(Flux.fromIterable[T])
  }
}

object DynamoDbTable extends AsJavaExtensions {
  sealed trait ConditionEntry {
    val value: (String, Condition)
  }

  case class ConditionEntryString(
    key: String,
    queryValue: String,
    operator: ComparisonOperator = ComparisonOperator.EQ
  ) extends ConditionEntry {
    override val value: (String, Condition) = (
      key,
      Condition.builder()
        .comparisonOperator(operator)
        .attributeValueList(AttributeValue.builder().s(queryValue).build())
        .build()
    )
  }

  case class UpdateContextContainer(
    updateExpression: String,
    conditionExpression: String,
    expressionAttributeNames: Map[String, String],
    expressionAttributeValues: Map[String, AttributeValue]
  )

  object UpdateContext {
    def builder[T](
      props: Map[String, Any],
      sortKey: Option[Any],
      serializer: DynamoDbSerializer[T]
    ): UpdateContextBuilder[T] = {
      new UpdateContextBuilder(props, sortKey, serializer)
    }
  }

  class UpdateContextBuilder[T](
    props: Map[String, Any],
    sortKey: Option[Any],
    serializer: DynamoDbSerializer[T]
  ) {
    private val serializerImpl = new DynamoDbSerializerAdapter[T](serializer)

    def build(): UpdateContextContainer = {
      val (nullProps, nonNullProps) = props.partition { case (_, v) => v == null }

      val removeExpression = if (nullProps.nonEmpty) {
        "REMOVE " + nullProps.keys.map(k => s"#$k").mkString(", ")
      } else ""

      // Build the UpdateContext, ExpressionAttributeNames, and ExpressionAttributeValues
      val (updateExpressionPart, expressionAttributeNames, expressionAttributeValues) =
        nonNullProps.foldLeft(("", Map.empty[String, String], Map.empty[String, AttributeValue])) {
          case ((ue, ean, eav), (key, value)) =>
            val placeholderName = s"#$key"
            val placeholderValue = s":$key"
            val newUE = if (ue.isEmpty) s"$placeholderName = $placeholderValue" else s"$ue, $placeholderName = $placeholderValue"
            val newEAN = ean + (placeholderName -> key)
            val attributeValue = serializerImpl.toAttributeValue(value)
            val newEAV = eav + (placeholderValue -> attributeValue)
            (newUE, newEAN, newEAV)
        }

      // Combine RemoveExpression and UpdateContext
      val finalUpdateExpression = (removeExpression, updateExpressionPart) match {
        case (re, uep) if re.nonEmpty && uep.nonEmpty => s"$re SET $uep"
        case (re, _) if re.nonEmpty                   => re
        case (_, uep) if uep.nonEmpty                 => s"SET $uep"
        case _                                        => throw new IllegalArgumentException("No update to perform")
      }

      // Build the ConditionExpression
      val conditionExpression = (serializer.sortKeyName(), sortKey) match {
        case (Some(_), Some(_)) => "attribute_exists(#partitionKey) AND attribute_exists(#sortKey)"
        case _                  => "attribute_exists(#partitionKey)"
      }

      // Build ExpressionAttributeNames for key attributes
      val keyAttributeNames: Map[String, String] = (serializer.sortKeyName(), sortKey) match {
        case (Some(skName), Some(_)) => Map("#partitionKey" -> serializer.keyName(), "#sortKey" -> skName)
        case _ => Map("#partitionKey" -> serializer.keyName())
      }

      UpdateContextContainer(
        updateExpression = finalUpdateExpression,
        conditionExpression = conditionExpression,
        expressionAttributeNames = expressionAttributeNames ++ keyAttributeNames,
        expressionAttributeValues = expressionAttributeValues
      )
    }
  }

  object CommonQueryRequest {
    def builder(
      keyConditionEntries: Seq[ConditionEntry],
      queryFilterEntries: Seq[ConditionEntry],
      orderAscending: Boolean,
      sizeOption: Option[Int],
      indexNameOption: Option[String],
      tableName: String
    ): CommonQueryRequestBuilder = {
      new CommonQueryRequestBuilder(
        keyConditionEntries,
        queryFilterEntries,
        orderAscending,
        sizeOption,
        indexNameOption,
        tableName
      )
    }
  }

  class CommonQueryRequestBuilder(
    keyConditionEntries: Seq[ConditionEntry],
    queryFilterEntries: Seq[ConditionEntry],
    orderAscending: Boolean,
    sizeOption: Option[Int],
    indexNameOption: Option[String],
    tableName: String
  ) {
    def build(): QueryRequest = {
      if (keyConditionEntries.isEmpty) {
        throw new IllegalArgumentException("At least one key condition must be provided.")
      }
      val keyConditions: Map[String, Condition] = keyConditionEntries.map(entry => entry.value).toMap
      val requestBuilder = QueryRequest.builder()
        .tableName(tableName)
        .scanIndexForward(orderAscending)
        .keyConditions(keyConditions.asJava)

      if (queryFilterEntries.nonEmpty) {
        requestBuilder.queryFilter(queryFilterEntries.map(entry => entry.value).toMap.asJava)
      }
      sizeOption.foreach(size => requestBuilder.limit(size))
      indexNameOption.foreach(indexName => requestBuilder.indexName(indexName))

      requestBuilder.build()
    }
  }
}

trait DynamoDbSerializer[T] {
  def deserialize(item: scala.collection.immutable.Map[String, Any]): T
  def keyName(): String
  def sortKeyName(): Option[String] = None
  def serialize(item: T): scala.collection.immutable.Map[String, AttributeValue]
}

private[aws] class DynamoDbSerializerAdapter[T](
  private val serializer: DynamoDbSerializer[T]
) extends AsJavaExtensions with AsScalaExtensions {
  def toKey(primaryKey: Any, sortKey: Option[Any]): java.util.Map[String, AttributeValue] = {
    val keyMap = sortKey match {
      case Some(sk) if serializer.sortKeyName().isDefined => Map(
        serializer.keyName() -> toAttributeValue(primaryKey),
        serializer.sortKeyName().get -> toAttributeValue(sk))
      case _ => Map(serializer.keyName() -> toAttributeValue(primaryKey))
    }
    keyMap.asJava
  }

  def toAttributeValue(value: Any): AttributeValue = value match {
    case Some(v) => toAttributeValue(v)
    case None    => AttributeValue.builder().nul(true).build() // Convert `None` to a DynamoDB NULL.
    case s: String => AttributeValue.builder().s(s).build()
    case n: Number => AttributeValue.builder().n(n.toString).build()
    case b: Boolean => AttributeValue.builder().bool(b).build()
    case l: Seq[Any] => AttributeValue.builder().l(l.map(toAttributeValue).asJava).build()
    case m: Map[_, _] if m.nonEmpty =>
      AttributeValue.builder()
        .m(m.view.mapValues(toAttributeValue).toMap.asInstanceOf[Map[String, AttributeValue]].asJava)
        .build()
    case m: Map[_, _] if m.isEmpty =>
      AttributeValue.builder().m(Map.empty[String, AttributeValue].asJava).build()
    case _ => throw new IllegalArgumentException(s"Unsupported attribute value type: $value")
  }

  private def deserializeAttributeValue(value: AttributeValue): Any = {
    if (value.s() != null) value.s()
    else if (value.n() != null) value.n()
    else if (value.b() != null) value.b().asByteArray()
    else if (value.hasSs) value.ss().asScala.toSet.asInstanceOf[Serializable]
    else if (value.hasNs) value.ns().asScala.toSet.asInstanceOf[Serializable]
    else if (value.hasBs) value.bs().asScala.map(_.asByteArray()).toSet.asInstanceOf[Serializable]
    else if (value.hasM) deserializeMap(value.m().asScala.toMap).asInstanceOf[Serializable]
    else if (value.hasL) value.l().asScala.map(deserializeAttributeValue).toList
    else if (Option(value.bool()).isDefined) value.bool()
    else if (Option(value.nul()).isDefined) value.nul()
    else throw new ApplicationException(s"Unsupported AttributeValue type")
  }

  private def deserializeMap(item: Map[String, AttributeValue]): scala.collection.immutable.Map[String, Any] = {
    item.map {
      case (name, value) => name -> deserializeAttributeValue(value)
    }
  }

  def deserializeItem(item: java.util.Map[String, AttributeValue]): T = {
    serializer.deserialize(deserializeMap(item.asScala.toMap))
  }

  def deserializeItems(items: java.util.List[java.util.Map[String, AttributeValue]]): java.util.List[T] = {
    items.asScala.map(deserializeItem).asJava
  }

  def serializeItem(item: T): scala.collection.immutable.Map[String, AttributeValue] = {
    serializer.serialize(item)
  }
}
