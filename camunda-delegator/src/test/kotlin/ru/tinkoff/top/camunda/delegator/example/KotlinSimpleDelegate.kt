package ru.tinkoff.top.camunda.delegator.example

import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.annotations.Variable
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultVariable

@CamundaDelegate("kotlinSimpleDelegate")
class KotlinSimpleDelegate {

    @Suppress("UnusedPrivateMember", "FunctionOnlyReturningConstant")
    @DelegateExecute
    @SingleResultVariable("delegateResult")
    fun params(
        @Variable testRequired: String,
        @Variable testOptional: String?
    ): String = "delegateResult"
}
