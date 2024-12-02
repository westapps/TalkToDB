package com.westapps.talktodb.configs.validation

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.bind.support.WebExchangeBindException

import java.util
import java.util.stream.Collectors

@ControllerAdvice
class ValidationHandler {
  @ExceptionHandler(Array(classOf[WebExchangeBindException]))
  def handleException(e: WebExchangeBindException): ResponseEntity[util.List[String]] = {
    val errors =
      e.getBindingResult
        .getFieldErrors
        .stream
        .map(e => s"${e.getField} - > ${e.getDefaultMessage}")
        .collect(Collectors.toList[String])

    ResponseEntity.badRequest.body(errors)
  }
}
