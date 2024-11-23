package com.westapps.talktodb.exceptions

import com.networknt.schema.ValidationMessage

import java.util

final case class JsonValidationException(
  validationMessage: util.Set[ValidationMessage]
) extends RuntimeException
