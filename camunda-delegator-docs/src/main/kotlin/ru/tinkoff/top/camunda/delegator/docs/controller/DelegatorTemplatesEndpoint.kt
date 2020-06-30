package ru.tinkoff.top.camunda.delegator.docs.controller

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import ru.tinkoff.top.camunda.delegator.docs.templates.CamundaTemplateLoader

/**
 * Provide camunda templates for modeler
* */
@Endpoint(id = "camunda-delegator-templates")
class DelegatorTemplatesEndpoint(
    private val templatesLoaders: List<CamundaTemplateLoader>
) {

    @ReadOperation(produces = ["application/json"])
    fun delegatesTemplates() = templates

    private val templates: String by lazy {
        objectWriter.writeValueAsString(templatesLoaders.flatMap { it.load() })
    }
}
