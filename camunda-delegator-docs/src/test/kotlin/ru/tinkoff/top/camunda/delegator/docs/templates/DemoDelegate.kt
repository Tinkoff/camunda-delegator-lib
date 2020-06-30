package ru.tinkoff.top.camunda.delegator.docs.templates

import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.annotations.Variable
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.MultipleResults
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.ResultVariable
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultVariable
import java.io.Serializable

@CamundaDelegate("demoDelegate")
class DemoDelegate {

    @DelegateExecute
    @SingleResultVariable("result")
    @MultipleResults
    fun generateProductPriority(
        @Variable("string") testString: String,
        @Variable("int") testInt: Int?
    ): TestResult {
        return TestResult(
            testString = testString,
            testInt = testInt,
            testBoolean = false
        )
    }
}

data class TestResult(
    @ResultVariable("testString")
    val testString: String,
    val testInt: Int?,
    val testBoolean: Boolean
) : Serializable
