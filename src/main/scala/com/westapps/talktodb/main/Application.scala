package com.westapps.talktodb

import com.typesafe.scalalogging.LazyLogging
import com.westapps.talktodb.configs.AppConfig
import com.westapps.talktodb.configs.AwsConfig
import com.westapps.talktodb.configs.IntegrationConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication(scanBasePackages = Array("com.westapps.talktodb"))
@EnableConfigurationProperties(value = Array(classOf[AppConfig], classOf[AwsConfig], classOf[IntegrationConfig]))
class Application

object TalkToDBApplication extends LazyLogging {
  def main(args: Array[String]): Unit = {
    try {
      SpringApplication.run(classOf[Application], args: _*)
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        throw ex
    }
  }
}