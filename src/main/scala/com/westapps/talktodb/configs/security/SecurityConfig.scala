package com.westapps.talktodb.configs.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.scalalogging.LazyLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono

import java.util.Collections
import scala.jdk.CollectionConverters._

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
class SecurityConfig(@Autowired securityContextRepository: ServerSecurityContextRepository) extends LazyLogging {

  private val AUTH_TOKEN = "99e2e3b8-c89c-426b-b2ac-da0e18e9c9b2"

  @Bean
  def securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = {
    // Apply CORS configuration, CORS configuration must be applied before other authentications.
    http.cors().configurationSource(corsConfigurationSource())

    // Configure security rules
    http.authorizeExchange()
      .pathMatchers(HttpMethod.OPTIONS).permitAll() // Permit preflight requests -- HttpMethod.OPTIONS
      .pathMatchers("/api/v1/**").authenticated()
      .anyExchange().permitAll()

    // Disable default security mechanisms
    http
      .httpBasic().disable()
      .formLogin().disable()
      .logout().disable()
      .csrf().disable()

    // Set security context repository and add authentication filter
    http.securityContextRepository(securityContextRepository)
    http.addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)

    // Configure exception handling
    http.exceptionHandling()
      .accessDeniedHandler((exchange, denied) => {
        val response = exchange.getResponse
        response.setStatusCode(HttpStatus.FORBIDDEN)
        response.getHeaders.setContentType(MediaType.APPLICATION_JSON)
        val body = Map("type" -> "AccessDeniedException", "message" -> "Access Denied")
        val bytes = new ObjectMapper().writeValueAsBytes(body.asJava)
        val buffer = response.bufferFactory().wrap(bytes)
        response.writeWith(Mono.just(buffer))
      })
      .authenticationEntryPoint((exchange, authException) => {
        val response = exchange.getResponse
        response.setStatusCode(HttpStatus.UNAUTHORIZED)
        response.getHeaders.setContentType(MediaType.APPLICATION_JSON)
        val body = Map("type" -> "AuthenticationException", "message" -> "Unauthorized")
        val bytes = new ObjectMapper().writeValueAsBytes(body.asJava)
        val buffer = response.bufferFactory().wrap(bytes)
        response.writeWith(Mono.just(buffer))
      })

    http.build()
  }

  @Bean
  def corsConfigurationSource(): CorsConfigurationSource = {//CORS allowed origins
    val configuration = new CorsConfiguration()
    configuration.setAllowedOrigins(
      java.util.List.of(
        "http://resume.simonxie.net",
        "https://resume.simonxie.net",
        "http://localhost"
      )
    )
    configuration.setAllowedMethods(
      java.util.List.of("GET", "POST", "DELETE", "PATCH", "OPTIONS") // Including OPTIONS for preflight
    )
    configuration.setAllowedHeaders(
      java.util.List.of("Authorization", "Content-Type") // Added "Content-Type"
    )
    configuration.setAllowCredentials(true) // Optional: Enable if needed

    val source = new UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", configuration)

    source
  }


  private def authenticationWebFilter(): AuthenticationWebFilter = {//header Authentication + Bearer + token
    val authManager: ReactiveAuthenticationManager = (authentication: Authentication) => {
      val token = authentication.getCredentials.asInstanceOf[String]
      if (AUTH_TOKEN == token) {
        // Return a fully authenticated token
        Mono.just(new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList()))
      } else {
        Mono.error(new AccessDeniedException("Invalid or missing token"))
      }
    }

    val authConverter: ServerAuthenticationConverter = exchange => {
      val authHeader = exchange.getRequest.getHeaders.getFirst("Authorization")
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        val token = authHeader.substring(7)
        Mono.just(new UsernamePasswordAuthenticationToken(null, token))
      } else {
        Mono.error(new AccessDeniedException("Missing or invalid Authorization header"))
      }
    }

    val authFilter = new AuthenticationWebFilter(authManager)
    authFilter.setServerAuthenticationConverter(authConverter)
    authFilter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository())
    authFilter
  }
}