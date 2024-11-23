package com.westapps.talktodb.configs

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

import scala.beans.BeanProperty

@ConfigurationProperties(prefix = "integration")
@Validated
class IntegrationConfig {
  @NotBlank
  @BeanProperty
  var dataApiUrl: String = _
}
