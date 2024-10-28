package com.westapps.talktodb

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = Array("com.westapps.talktodb"))
class Application

object TalkToDBApplication extends App {
  SpringApplication.run(classOf[Application], args: _*)
}