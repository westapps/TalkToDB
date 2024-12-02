package com.westapps.talktodb.exceptions

import com.westapps.talktodb.exceptions.Identity.Identity


class ResourceInactiveException(val name: String, val identities: Map[Identity, String] = Map.empty)
  extends RuntimeException(s"${name} is inactive")
