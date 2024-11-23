package com.westapps.talktodb

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
@SpringBootApplication(scanBasePackages = Array("com.westapps.talktodb"))
class LocalMainApplication

object LocalMain {
  def main(args: Array[String]): Unit = {
    val app = new SpringApplication(classOf[LocalMainApplication])
    app.setAdditionalProfiles("test")
    app.run(args: _*)
  }
}
