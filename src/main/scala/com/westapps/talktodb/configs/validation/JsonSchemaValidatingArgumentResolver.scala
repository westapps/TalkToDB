package com.westapps.talktodb.configs.validation

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.typesafe.scalalogging.LazyLogging
import com.westapps.talktodb.configs.validation.JsonSchemaValidatingArgumentResolver.getSchema
import com.westapps.talktodb.configs.validation.JsonSchemaValidatingArgumentResolver.objectMapper
import com.westapps.talktodb.configs.validation.JsonSchemaValidatingArgumentResolver.scalaMapper
import org.springframework.core.MethodParameter
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.util.StreamUtils
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

import java.lang.annotation.Annotation
import java.nio.charset.StandardCharsets

object JsonSchemaValidatingArgumentResolver extends LazyLogging {
  private val objectMapper = new ObjectMapper()
  private val schemaGenerator = new JsonSchemaGenerator(objectMapper)
  private val schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
  private val scalaMapper = JsonMapper.builder().addModule(DefaultScalaModule)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build()
  private val schemaCache: Cache[String, JsonSchema] =
    Caffeine.newBuilder()
      .recordStats()
      .maximumSize(50)
      .build();

  private def getSchema(parameterType: Class[?]): JsonSchema = {
    schemaCache.getIfPresent(parameterType.getName) match {
      case jsonSchema: JsonSchema => jsonSchema
      case _ =>{
        val jsonNode = schemaGenerator.generateJsonSchema(parameterType)
        val jsonSchema = schemaFactory.getSchema(jsonNode)
        schemaCache.put(parameterType.getName, jsonSchema)
        jsonSchema
      }
    }
  }
}

class JsonSchemaValidatingArgumentResolver extends HandlerMethodArgumentResolver {

  override def supportsParameter(parameter: MethodParameter): Boolean = {
    parameter.getParameterAnnotation(classOf[ValidJson]) match {
      case _: Annotation => true
      case _ => false
    }
  }

  override def resolveArgument(
    parameter: MethodParameter,
    bindingContext: BindingContext,
    exchange: ServerWebExchange
  ): Mono[Any] = {
    getJsonPayload(exchange).map(payload => {
      val jsonSchema = getSchema(parameter.getParameterType)
      val json = objectMapper.readTree(payload)
      val validationResult = jsonSchema.validate(json)
      if (validationResult.isEmpty) {
        scalaMapper.readValue(payload, parameter.getParameterType)
      } else {
        throw JsonValidationException(validationResult)
      }
    })
  }

  private def getJsonPayload(exchange: ServerWebExchange): Mono[String] = {
    DataBufferUtils.join(exchange.getRequest.getBody)
      .map(dataBuffer => StreamUtils.copyToString(dataBuffer.asInputStream(), StandardCharsets.UTF_8))
  }
}
