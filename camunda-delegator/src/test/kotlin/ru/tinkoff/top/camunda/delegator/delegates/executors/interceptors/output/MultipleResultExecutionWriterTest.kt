package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutorImpl
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.MultipleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.MultipleResults
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.ResultVariable
import kotlin.reflect.jvm.javaMethod

class MultipleResultExecutionWriterTest {

    private val execution = mock<DelegateExecution>()

    @Test
    fun `when delegate throw error then interceptor write null result & rethrow error`() {
        class DelegateResult(
            @ResultVariable
            val first: String
        )

        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            @MultipleResults
            fun test(): DelegateResult = throw IllegalArgumentException("")
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = MultipleResultExecutionWriter().also {
            it.nextExecutor = DelegateExecutorImpl()
        }

        assertThrows<IllegalArgumentException> {
            interceptor.execute(execution, delegateInformation, emptyArray())
        }

        verify(execution).setVariableLocal(eq("first"), isNull())
    }

    @Test
    fun `when delegate return value then interceptor write result`() {
        class DelegateResult(
            @ResultVariable
            val first: String,

            @ResultVariable("customName")
            val firstOverride: Int,

            @ResultVariable(isLocal = false)
            val global: String,

            @ResultVariable("globalCustomName", isLocal = false)
            val globalOverride: Boolean
        )

        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            @MultipleResults
            fun test() = DelegateResult("value", 7, "global", true)
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = MultipleResultExecutionWriter().also {
            it.nextExecutor = DelegateExecutorImpl()
        }

        interceptor.execute(execution, delegateInformation, emptyArray())

        verify(execution).setVariableLocal(eq("first"), eq("value"))
        verify(execution).setVariableLocal(eq("customName"), eq(7))
        verify(execution).setVariable(eq("global"), eq("global"))
        verify(execution).setVariable(eq("globalCustomName"), eq(true))
    }

    @Test
    fun `when delegate return value void then interceptor not write result`() {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            @MultipleResults
            fun test() = Unit
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = MultipleResultExecutionWriter().also {
            it.nextExecutor = DelegateExecutorImpl()
        }

        interceptor.execute(execution, delegateInformation, emptyArray())

        verify(execution, never()).setVariableLocal(any(), any())
        verify(execution, never()).setVariable(any(), any())
    }
}
