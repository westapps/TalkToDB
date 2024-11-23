package com.westapps.talktodb.services

import com.westapps.talktodb.domain.Greeting
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GreetingService {
  def getGreeting: Mono[Greeting] = {
    Mono.just(Greeting("Hello, welcome to our reactive Scala API!"))
  }
}
