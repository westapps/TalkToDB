package com.westapps.talktodb.aws

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.westapps.talktodb.aws.SqsClient.JsonMessage
import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

class JsonMessageTest {

  private val mapper = new ObjectMapper().registerModule(DefaultScalaModule)

  @Test
  def testStrValWithValidJson(): Unit = {
    val jsonString = """{"key":"value","number":42}"""
    val jsonNode: JsonNode = mapper.readTree(jsonString)
    val jsonMessage = JsonMessage(jsonNode)

    val result: String = jsonMessage.strVal()

    assertEquals(jsonString, result, "JsonMessage strVal() should return the correct JSON string")
  }

  @Test
  def testStrValWithEmptyJson(): Unit = {
    val jsonString = "{}"
    val jsonNode: JsonNode = mapper.readTree(jsonString)
    val jsonMessage = JsonMessage(jsonNode)

    val result = jsonMessage.strVal()

    assertEquals(jsonString, result, "JsonMessage strVal() should return an empty JSON object string")
  }

  @Test
  def testStrValWithNestedJson(): Unit = {
    val jsonString = """{"outer":{"inner":"value"}}"""
    val jsonNode: JsonNode = mapper.readTree(jsonString)
    val jsonMessage = JsonMessage(jsonNode)

    val result = jsonMessage.strVal()

    assertEquals(jsonString, result, "JsonMessage strVal() should return nested JSON as string")
  }
}

