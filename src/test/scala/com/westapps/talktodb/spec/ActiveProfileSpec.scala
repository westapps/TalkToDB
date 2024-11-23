package com.westapps.talktodb.spec

import com.typesafe.scalalogging.LazyLogging
import org.springframework.test.context.ActiveProfiles
@ActiveProfiles(profiles = Array("test"))
trait ActiveProfileSpec extends LazyLogging {}
