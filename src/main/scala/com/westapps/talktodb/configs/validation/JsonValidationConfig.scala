package com.westapps.talktodb.configs.validation

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class JsonValidationConfig extends WebFluxConfigurer {
  override def configureArgumentResolvers(configurer: ArgumentResolverConfigurer): Unit = {
    configurer.addCustomResolver(new JsonSchemaValidatingArgumentResolver())
  }
}
