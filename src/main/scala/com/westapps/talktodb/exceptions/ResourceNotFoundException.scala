package com.westapps.talktodb.exceptions

import com.westapps.talktodb.exceptions.Identity.Identity


object Identity extends Enumeration {
  type Identity = Value
  val SessionKey = Value
}

class ResourceNotFoundException(val name: String, val identities: Map[Identity, String] = Map.empty)
  extends RuntimeException(s"${name} not found")

class SessionNotFoundException(identities: Map[Identity, String] = Map.empty)
  extends ResourceNotFoundException("Session", identities)

class AssistantNotFoundException(identities: Map[Identity, String] = Map.empty)
  extends ResourceNotFoundException("Assistant", identities)

class ThreadNotFoundException(identities: Map[Identity, String] = Map.empty)
  extends ResourceNotFoundException("Thread", identities)

class MessageNotFoundException(identities: Map[Identity, String] = Map.empty)
  extends ResourceNotFoundException("Message", identities)

class InvalidResourceException(val name: String, val identities: Map[Identity, String] = Map.empty)
  extends RuntimeException(s"${name} is invalid")

class InvalidHmacSignatureException(identities: Map[Identity, String] = Map.empty)
  extends InvalidResourceException("Hmac Signature", identities)
