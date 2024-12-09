//package com.westapps.talktodb.configs
//
//import org.springframework.context.annotation.{Bean, Configuration}
//import org.springframework.core.annotation.Order
//import org.springframework.http.HttpMethod
//import org.springframework.http.HttpStatus
//import org.springframework.web.server.{ServerWebExchange, WebFilter, WebFilterChain}
//import reactor.core.publisher.Mono
//
//@Configuration
//@Order(-2) // ensure this runs very early
//class ForceCorsFilter {
//
//  @Bean
//  def forceCorsFilter2(): WebFilter = new WebFilter {
//    override def filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono[Void] = {
//      val response = exchange.getResponse
//      val headers = response.getHeaders
//      headers.add("Access-Control-Allow-Origin", "http://resume.simonxie.net")
//      headers.add("Access-Control-Allow-Origin", "https://resume.simonxie.net")
//      headers.add("Access-Control-Allow-Headers", "*")
//      headers.add("Access-Control-Allow-Methods", "*")
//      headers.add("Access-Control-Allow-Credentials", "true")
//
//      // If it's a preflight request (OPTIONS), respond immediately
//      if (exchange.getRequest.getMethod == HttpMethod.OPTIONS) {
//        response.setStatusCode(HttpStatus.OK)
//        return Mono.empty()
//      }
//
//      chain.filter(exchange)
//    }
//  }
//}
