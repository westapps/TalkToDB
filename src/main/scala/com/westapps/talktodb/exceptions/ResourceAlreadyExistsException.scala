package com.westapps.talktodb.exceptions

import com.westapps.talktodb.exceptions.Identity.Identity


class ResourceAlreadyExistsException(val name: String, val identities: Map[Identity, String] = Map.empty)
  extends RuntimeException(s"${name} is found")
