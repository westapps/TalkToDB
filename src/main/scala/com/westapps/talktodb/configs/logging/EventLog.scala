package com.westapps.talktodb.configs.logging


import com.westapps.talktodb.configs.json.JsonSupportMapperHelper

import scala.language.implicitConversions

case class EventLog(eventType: String, fields: (String, Any)*)

object EventLog {
  private val objectMapper = JsonSupportMapperHelper.createObjectMapper

  implicit def fromEventLogToString(source: EventLog): String = {
    objectMapper.writeValueAsString(source)
  }
}
