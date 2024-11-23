package com.westapps.talktodb.main

import com.westapps.talktodb.aws.EmailClient
import com.westapps.talktodb.configs.AppConfig
import com.westapps.talktodb.configs.AwsConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesAsyncClient

@Configuration
class ServiceAssembler(
  private val appConfig: AppConfig,
  private val awsConfig: AwsConfig,
  //private val integrationConfig: IntegrationConfig
) {
  private def defaultCredentialsProvider: DefaultCredentialsProvider = {
    DefaultCredentialsProvider.create()
  }

  @Bean
  def sesAsyncClient(): SesAsyncClient = {
    SesAsyncClient.builder()
      .region(Region.of(awsConfig.region))
      .credentialsProvider(defaultCredentialsProvider)
      .build()
  }

  @Bean
  def createEmailClient(sesAsyncClient: SesAsyncClient): EmailClient = {
    new EmailClient(
      sesAsyncClient,
      awsConfig.ses.senderEmail,
      awsConfig.ses.replyToEmail,
      appConfig.name
    )
  }
}
