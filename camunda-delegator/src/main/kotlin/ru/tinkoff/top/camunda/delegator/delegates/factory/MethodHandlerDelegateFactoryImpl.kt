package ru.tinkoff.top.camunda.delegator.delegates.factory

import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.MethodHandlerDelegateImpl
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.servicetask.MethodHandlerDelegate

class MethodHandlerDelegateFactoryImpl : MethodHandlerDelegateFactory {

    override fun createMethodHandlerDelegate(
        delegateInformation: DelegateInformation,
        delegateExecutor: DelegateExecutor
    ): MethodHandlerDelegate {
        return MethodHandlerDelegateImpl(
            delegateInformation = delegateInformation,
            delegateExecutor = delegateExecutor
        )
    }
}
