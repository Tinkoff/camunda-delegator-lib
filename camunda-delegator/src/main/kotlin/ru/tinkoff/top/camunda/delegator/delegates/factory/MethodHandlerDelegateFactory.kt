package ru.tinkoff.top.camunda.delegator.delegates.factory

import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.servicetask.MethodHandlerDelegate

interface MethodHandlerDelegateFactory {

    fun createMethodHandlerDelegate(
        delegateInformation: DelegateInformation,
        delegateExecutor: DelegateExecutor
    ): MethodHandlerDelegate
}
