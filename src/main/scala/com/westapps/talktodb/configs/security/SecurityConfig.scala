package com.westapps.talktodb.configs.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
class SecurityConfig(
  @Autowired securityContextRepository: ServerSecurityContextRepository
) {

  @Bean
  def securityWebFilterChain(
    http: ServerHttpSecurity
  ): SecurityWebFilterChain = {
    http.httpBasic().disable()
    http.formLogin().disable()
    http.logout().disable()
    http.csrf().disable()
    http.securityContextRepository(securityContextRepository)

    http.build()
  }
}
