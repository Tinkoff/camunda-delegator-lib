package ru.tinkoff.top.camunda.delegator.docs.controller

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter

val objectWriter: ObjectWriter = ObjectMapper()
    .findAndRegisterModules()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .writerWithDefaultPrettyPrinter()
