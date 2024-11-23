package com.westapps.talktodb.configs

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

@Component
@ConfigurationProperties(prefix = "app")
class AppConfig {
  @NotBlank
  @BeanProperty
  var name: String = _

  @NotNull
  @BeanProperty
  var version: String = _
}