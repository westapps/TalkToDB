package com.westapps.talktodb.configs.validation

import com.networknt.schema.ValidationMessage

import java.util

final case class JsonValidationException(
  validationMessages: util.Set[ValidationMessage]
) extends RuntimeException
