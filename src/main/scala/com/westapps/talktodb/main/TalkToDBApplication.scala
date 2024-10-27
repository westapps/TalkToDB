package com.westapps.talktodb.main

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@PropertySource(Array("classpath:application.properties"))
class Application

object Application {
  def main(args: Array[String]): Unit = {
    runApplication[Application](args)
  }
}
