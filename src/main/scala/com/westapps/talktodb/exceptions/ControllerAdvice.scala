package com.westapps.talktodb.exceptions

import com.typesafe.scalalogging.LazyLogging
import com.westapps.talktodb.configs.logging.EventLog
import com.westapps.talktodb.configs.validation.JsonValidationException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.MissingRequestValueException
import org.springframework.web.server.ServerWebInputException

import java.io.PrintWriter
import java.io.StringWriter
import java.util
import scala.jdk.CollectionConverters.MapHasAsJava

@RestControllerAdvice
class ControllerAdvice extends LazyLogging {

  @ExceptionHandler(value = Array(classOf[JsonValidationException]))
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  def exceptionHandler(ex: JsonValidationException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "JsonValidationException",
      "message", "Request body validation error",
      "details", ex.validationMessages.stream().map(error =>
        util.Map.of("field", error.getPath, "message", error.getMessage))
    )
  }

  @ExceptionHandler(value = Array(classOf[AccessDeniedException]))
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  @ResponseBody
  def exceptionHandler(ex: AccessDeniedException): util.Map[String, Object] = {
    util.Map.of(
      "type", "AccessDeniedException",
      "message", "Access Denied"
    )
  }

  @ExceptionHandler(value = Array(classOf[ResourceNotFoundException]))
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ResponseBody
  def exceptionHandler(ex: ResourceNotFoundException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "ResourceNotFoundException",
      "message", s"${ex.name} is not found",
      "details", ex.identities.asJava
    )
  }

  @ExceptionHandler(value = Array(classOf[InvalidHmacSignatureException]))
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  @ResponseBody
  def exceptionHandler(ex: InvalidHmacSignatureException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "InvalidHmacSignatureException",
      "message", s"${ex.name} is invalid",
      "details", ex.identities.asJava
    )
  }

  @ExceptionHandler(value = Array(classOf[SessionNotFoundException]))
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  @ResponseBody
  def exceptionHandler(ex: SessionNotFoundException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    exceptionHandler(ex.asInstanceOf[ResourceNotFoundException])
  }

  @ExceptionHandler(value = Array(classOf[WebExchangeBindException]))
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  def exceptionHandler(ex: WebExchangeBindException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "WebExchangeBindException",
      "message", "Data binding and validation error",
      "details", ex.getBindingResult.getFieldErrors.stream.map(error =>
        util.Map.of("field", error.getField, "message", error.getDefaultMessage)))
  }

  @ExceptionHandler(value = Array(classOf[MissingRequestValueException]))
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  def exceptionHandler(ex: MissingRequestValueException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "MissingRequestValueException",
      "message", "Missing request value error",
      "details", util.Map.of("field", ex.getName, "message", ex.getReason))
  }

  @ExceptionHandler(value = Array(classOf[ServerWebInputException]))
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  def exceptionHandler(ex: ServerWebInputException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "ServerWebInputException",
      "message", "Missing request value error",
      "details", util.Map.of("field", ex.getMethodParameter.getParameter.getName, "message", ex.getReason))
  }

  @ExceptionHandler(value = Array(classOf[ConstraintViolationException]))
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  def exceptionHandler(ex: ConstraintViolationException): util.Map[String, Object] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "ConstraintViolationException",
      "message", ex.getMessage
    )
  }

  @ExceptionHandler(Array(classOf[IllegalArgumentException]))
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  def handleIllegalArgumentException(ex: IllegalArgumentException): util.Map[String, String] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "IllegalArgumentException",
      "message", "Request body has invalid argument",
      "details", ex.getMessage
    )
  }

  @ExceptionHandler(Array(classOf[IllegalStateException]))
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  def handleIllegalStateException(ex: IllegalStateException): util.Map[String, String] = {
    logger.error(ex.getMessage, ex)
    util.Map.of(
      "type", "IllegalStateException",
      "message", "No results found for given argument",
      "details", ex.getMessage
    )
  }

  @ExceptionHandler(value = Array(classOf[Exception]))
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  def unhandledExceptionHandler(ex: Exception): util.Map[String, Object] = {
    val referenceId = newReferenceId()
    val sw = new StringWriter()
    ex.printStackTrace(new PrintWriter(sw))
    logger.error(EventLog("UnhandledApiError",
      "Reference" -> referenceId,
      "Error" -> ex.getMessage,
      "Stack" -> sw.toString
    ), ex)
    util.Map.of(
      "type", "UnhandledException",
      "message", "Internal server error",
      "details", util.Map.of("reference", referenceId, "message", Option(ex.getMessage).getOrElse("No Message"))
    )
  }

  private def newReferenceId(): String = {
    util.UUID.randomUUID().toString
  }
}
