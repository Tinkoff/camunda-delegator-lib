package ru.tinkoff.top.camunda.delegator.docs.controller

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import ru.tinkoff.top.camunda.delegator.docs.DelegateDocService

/**
 * Provide camunda delegator documentation
 * */
@Endpoint(id = "camunda-delegator-docs")
class DelegatorDocsEndpoint(
    private val delegateDocService: DelegateDocService
) {

    @ReadOperation(produces = ["application/json"])
    fun delegatesDoc() = doc

    private val doc: String by lazy {
        objectWriter.writeValueAsString(delegateDocService.getDelegatesDoc())
    }
}
