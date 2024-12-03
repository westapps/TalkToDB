package com.westapps.talktodb.clients

import com.typesafe.scalalogging.LazyLogging
import com.westapps.talktodb.configs.logging.EventLog
import com.westapps.talktodb.exceptions.Identity.Identity
import com.westapps.talktodb.exceptions.ResourceInactiveException
import com.westapps.talktodb.exceptions.WebClientResponseWithReferenceException
import org.springframework.http.{HttpStatus, HttpStatusCode}
import org.springframework.web.reactive.function.client.{WebClientRequestException, WebClientResponseException}
import reactor.core.publisher.Mono

import java.util

trait WebClientExceptionHandler extends LazyLogging {
  def handleErrorAsOption[T](
    throwable: Throwable,
    eventType: String,
    identities: Map[Identity, String] = Map.empty): Mono[Option[T]] = {
    val idContexts = identities.map { case (key, value) => key.toString -> value }
    logger.error(EventLog(eventType, idContexts.toList :+ ("Error", throwable) : _*))
    throwable match {
      case ex: WebClientResponseException => {
        if (ex.getStatusCode.isSameCodeAs(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value())))
          Mono.error(new ResourceInactiveException(eventType, identities))
        else
        Mono.just(None)
      }
      case e: WebClientRequestException => Mono.just(None)
    }
  }

  def handleErrorAsThrowable[T](
    throwable: Throwable,
    eventType: String,
    identities: Map[Identity, String] = Map.empty): Mono[T] = {
    val idContexts = identities.map { case (key, value) => key.toString -> value }
    val referenceId = util.UUID.randomUUID().toString
    logger.error(EventLog(eventType, idContexts.toList :+ ("Error", throwable) :+ ("ReferenceId", referenceId) : _*))
    throwable match {
      case ex: WebClientResponseException => Mono.error(new WebClientResponseWithReferenceException(ex, referenceId, identities))
      case _ => Mono.error(throwable)
    }
  }
}