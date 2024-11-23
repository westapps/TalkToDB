package com.westapps.talktodb.configs.security

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class CustomSecurityContextRepository extends ServerSecurityContextRepository {

  override def save(exchange: ServerWebExchange, context: SecurityContext): Mono[Void] = {
    // Implement save logic if necessary
    Mono.empty()
  }

  override def load(exchange: ServerWebExchange): Mono[SecurityContext] = {
    // Implement load logic, e.g., extracting token from headers
    Mono.empty()
  }
}