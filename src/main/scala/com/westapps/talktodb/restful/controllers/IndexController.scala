package com.westapps.talktodb.restful.controllers

import com.westapps.talktodb.configs.AppConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.{GetMapping, RestController}

@RestController
@RequestMapping(value = Array("/"))
class IndexController(
  @Autowired appConfig: AppConfig
) {

  @GetMapping(value = Array("/", ""))
  def hello(): String = {
    s"Welcome to ${appConfig.name}! ${appConfig.version}"
  }
}
