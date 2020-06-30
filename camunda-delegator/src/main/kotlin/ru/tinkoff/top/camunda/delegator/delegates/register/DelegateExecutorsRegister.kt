package ru.tinkoff.top.camunda.delegator.delegates.register

import ru.tinkoff.top.camunda.delegator.servicetask.MethodHandlerDelegate

interface DelegateExecutorsRegister {
    fun getDelegateMethodHandler(delegateName: String): MethodHandlerDelegate?
}
