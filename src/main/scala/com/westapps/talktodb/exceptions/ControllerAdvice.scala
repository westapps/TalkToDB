package com.westapps.talktodb.exceptions

import com.typesafe.scalalogging.LazyLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

import java.util

@RestControllerAdvice
class ControllerAdvice extends LazyLogging {
  @ExceptionHandler(value = Array(classOf[JsonValidationException]))
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  def exceptionhandler(ex: JsonValidationException): util.Map[String, Object] = {
    //logger.error(message = ex.getMessage, ex)//todo: macro applications do not support named and/or default arguments

    util.Map.of(
      "type", "JsonValidationException",
      "message", "Request body validation error",
      "details", ex.validationMessage.stream().map( error =>
        util.Map.of("field", error.getPath, "message", error.getMessage)
      )
    )
  }
}