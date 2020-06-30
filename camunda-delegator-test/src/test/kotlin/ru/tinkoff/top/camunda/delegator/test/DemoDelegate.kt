package ru.tinkoff.top.camunda.delegator.test

import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.annotations.Variable
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultVariable
import java.io.Serializable

@CamundaDelegate("demoDelegate")
class DemoDelegate {

    @DelegateExecute
    @SingleResultVariable("result")
    fun generateProductPriority(
        @Variable("string") testString: String,
        @Variable("int") testInt: Int,
        @Variable("boolean") testBoolean: Boolean
    ): TestResult {
        return TestResult(
            testString = testString,
            testInt = testInt,
            testBoolean = testBoolean
        )
    }
}

data class TestResult(
    val testString: String,
    val testInt: Int,
    val testBoolean: Boolean
) : Serializable
