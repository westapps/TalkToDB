package com.westapps.talktodb.configs

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.{CorsRegistry, EnableWebFlux, WebFluxConfigurer}

@Configuration
@EnableWebFlux
class WebFluxCorsConfig extends WebFluxConfigurer {
  override def addCorsMappings(registry: CorsRegistry): Unit = {
    registry.addMapping("/**")
      .allowedOrigins("http://resume.simonxie.net", "https://resume.simonxie.net")
      .allowedMethods("*")
      .allowedHeaders("*")
      .allowCredentials(true)
  }
}

