package com.westapps.talktodb.restful.controllers

import org.springframework.web.bind.annotation.{GetMapping, RestController}

@RestController
class HelloController {

  @GetMapping(Array("/"))
  def hello(): String = {
    "Welcome to TalkToDB!"
  }
}