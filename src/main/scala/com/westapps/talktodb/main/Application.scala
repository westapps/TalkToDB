package com.westapps.talktodb.main

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

object TalkToDBApplication extends App {
  SpringApplication.run(classOf[Application], args: _*)
}