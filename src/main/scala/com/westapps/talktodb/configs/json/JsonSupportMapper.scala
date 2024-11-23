package com.westapps.talktodb.configs.json

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.{LocalDateDeserializer, LocalDateTimeDeserializer}
import com.fasterxml.jackson.datatype.jsr310.ser.{LocalDateSerializer, LocalDateTimeSerializer}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.enumeratum.EnumeratumModule
import org.springframework.context.annotation.{Bean, Configuration, Primary}

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

@Configuration
class JsonSupportMapper {
  @Bean
  @Primary
  def jsonMapper: ObjectMapper = JsonSupportMapperHelper.createObjectMapper
}

object JsonSupportMapperHelper {
  def createObjectMapper: ObjectMapper = {
    val localDateTimeModule = new SimpleModule()

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val dateDeserializer = new LocalDateDeserializer(dateFormatter)
    val dateTimeDeserializer = new LocalDateTimeDeserializer(dateTimeFormatter)

    val dateSerializer = new LocalDateSerializer(dateFormatter)
    val dateTimeSerializer = new LocalDateTimeSerializer(dateTimeFormatter)

    localDateTimeModule
      .addSerializer(classOf[LocalDate], dateSerializer)
      .addSerializer(classOf[LocalDateTime], dateTimeSerializer)
      .addDeserializer(classOf[LocalDate], dateDeserializer)
      .addDeserializer(classOf[LocalDateTime], dateTimeDeserializer)

    val jsonMapper = JsonMapper.builder()
      .addModule(new Jdk8Module)
      .addModule(new JavaTimeModule)
      .addModule(DefaultScalaModule)
      .addModule(localDateTimeModule)
      .addModule(EnumeratumModule)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .build()

    jsonMapper
  }
}
