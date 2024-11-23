package com.westapps.talktodb.restful.controllers


import com.westapps.talktodb.domain.Greeting
import com.westapps.talktodb.services.GreetingService
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}
import reactor.core.publisher.Mono

@RestController
@RequestMapping(Array("/api/v1"))
class GreetingController(greetingService: GreetingService) {

  @GetMapping(Array("/greet"))
  def greet(): Mono[Greeting] = {
    greetingService.getGreeting
  }
}

