package com.westapps.talktodb.clients

import com.westapps.talktodb.configs.AwsConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region

import java.time.Duration

@Configuration
class AwsResources(
  @Autowired private val awsConfig: AwsConfig
) {
  val credentialsProvider = createCredentialProvider()
  val httpClient = createAsyncHttpClient()
  val region = Region.of(awsConfig.region)

  private def createCredentialProvider(): AwsCredentialsProvider = {
    DefaultCredentialsProvider.create()
  }

  private def createAsyncHttpClient(): SdkAsyncHttpClient =
    NettyNioAsyncHttpClient
      .builder
      .maxConcurrency(100)
      .connectionTimeout(Duration.ofSeconds(30))
      .connectionAcquisitionTimeout(Duration.ofSeconds(30))
      .readTimeout(Duration.ofSeconds(180))
      .build()
}
