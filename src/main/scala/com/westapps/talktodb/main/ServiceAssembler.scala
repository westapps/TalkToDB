package com.westapps.talktodb.main

import com.westapps.talktodb.aws.SesEmailClient
import com.westapps.talktodb.aws.ReactiveDynamoDbClient
import com.westapps.talktodb.clients.AwsResources
import com.westapps.talktodb.configs.AppConfig
import com.westapps.talktodb.configs.AwsConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.ses.SesAsyncClient

@Configuration
class ServiceAssembler(
  private val appConfig: AppConfig,
  private val awsConfig: AwsConfig,
  //private val integrationConfig: IntegrationConfig
) {
  private val awsResources = new AwsResources(awsConfig)
  //private val webClientFactory = new WebClientFactory(integrationConfig)

  private def defaultCredentialsProvider: DefaultCredentialsProvider = {
    DefaultCredentialsProvider.create()
  }

  @Bean
  def createDynamoDbAsyncClient(): DynamoDbAsyncClient = {
    DynamoDbAsyncClient.builder()
      .credentialsProvider(awsResources.credentialsProvider)
      .httpClient(awsResources.httpClient)
      .region(awsResources.region)
      .build()
  }

  @Bean
  def createReactiveDynamoDbClient(client: DynamoDbAsyncClient): ReactiveDynamoDbClient = {
    new ReactiveDynamoDbClient(client)
  }

  @Bean
  def sesAsyncClient(): SesAsyncClient = {
    SesAsyncClient.builder()
      .region(Region.of(awsConfig.region))
      .credentialsProvider(defaultCredentialsProvider)
      .build()
  }

  @Bean
  def createEmailClient(sesAsyncClient: SesAsyncClient): SesEmailClient = {
    new SesEmailClient(
      sesAsyncClient,
      awsConfig.ses.senderEmail,
      awsConfig.ses.replyToEmail,
      appConfig.name
    )
  }
}
