package com.westapps.talktodb.exceptions

import com.westapps.talktodb.exceptions.Identity.Identity
import org.springframework.web.reactive.function.client.WebClientResponseException

class WebClientResponseWithReferenceException(
  val originException: WebClientResponseException,
  val reference: String,
  val identities: Map[Identity, String] = Map.empty
) extends RuntimeException(s"Reference id: $reference - ${originException.getMessage}")