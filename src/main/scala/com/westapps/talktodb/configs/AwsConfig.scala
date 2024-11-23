package com.westapps.talktodb.configs

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

@Component
@ConfigurationProperties(prefix = "aws")
class AwsConfig {
  @NotBlank
  @BeanProperty
  var region: String = _

  @NotNull
  @BeanProperty
  @NestedConfigurationProperty
  var ses: SESConfig = _
}

@ConfigurationProperties(prefix = "aws.ses")
class SESConfig {
  @NotNull
  @BeanProperty
  var senderEmail: String = _

  @NotNull
  @BeanProperty
  var replyToEmail: String = _
}
