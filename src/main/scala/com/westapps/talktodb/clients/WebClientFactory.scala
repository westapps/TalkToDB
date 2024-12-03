package com.westapps.talktodb.clients

import com.typesafe.scalalogging.LazyLogging
import com.westapps.talktodb.configs.IntegrationConfig
import com.westapps.talktodb.configs.logging.EventLog
import io.netty.channel.ChannelOption
import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client._
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.util.retry.Retry

import java.time.Duration

class WebClientFactory(
  integrationConfig: IntegrationConfig
) extends LazyLogging {
  def dataApiClient: WebClient = {
    createWebClient(integrationConfig.dataApiUrl).build
  }

  private val maxInMemorySize = 10 * 1024 * 1024
  private def createWebClient(baseUrl: String): WebClient.Builder = {
    WebClient.builder
      .baseUrl(baseUrl)
      .clientConnector(new ReactorClientHttpConnector(httpClient))
      .codecs(configurer => configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
      .filter(log4xxFilter)
      .filter(retry5xxFilter)

  }

  private val connectionProvider: ConnectionProvider = ConnectionProvider
    .builder("fixed")
    .maxConnections(1000)
    .maxIdleTime(Duration.ofSeconds(20))
    .maxLifeTime(Duration.ofSeconds(60))
    .pendingAcquireTimeout(Duration.ofSeconds(60))
    .evictInBackground(Duration.ofSeconds(120))
    .build

  private val httpClient: HttpClient = HttpClient
    .create(connectionProvider)
    .resolver(DefaultAddressResolverGroup.INSTANCE)
    .option[Integer](ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
    .responseTimeout(Duration.ofSeconds(5))

  private val retry5xxFilter: ExchangeFilterFunction = {
    (request: ClientRequest, next: ExchangeFunction) => {
      next.exchange(request).flatMap {
        case response if response.statusCode.is5xxServerError => response.createException().flatMap(ex => {
          logger.error(EventLog("WebClient5xxError",
            ("message", ex.getMessage),
            ("responseBody", ex.getResponseBodyAsString)
          ))
          Mono.error(ex)
        })
        case response => Mono.just(response)
      }
      .retryWhen(Retry
        .backoff(3, Duration.ofMillis(100))
        .jitter(0.5d)
        .filter(exception => exception.isInstanceOf[WebClientResponseException])
        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) => retrySignal.failure())
      )
    }
  }

  private val log4xxFilter: ExchangeFilterFunction = {
    (request: ClientRequest, next: ExchangeFunction) => {
      next.exchange(request).flatMap {
        case response if response.statusCode.is4xxClientError() => response.createException().flatMap(ex => {
          logger.error(EventLog("WebClient4xxError",
            ("message", ex.getMessage),
            ("responseBody", ex.getResponseBodyAsString)
          ))
          Mono.error(ex)
        })
        case response => Mono.just(response)
      }
    }
  }
}

