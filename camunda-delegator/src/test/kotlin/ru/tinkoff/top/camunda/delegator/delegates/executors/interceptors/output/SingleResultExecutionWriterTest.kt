package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
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
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultVariable
import java.util.stream.Stream
import kotlin.reflect.jvm.javaMethod

class SingleResultExecutionWriterTest {

    private val execution = mock<DelegateExecution>()

    companion object {
        @JvmStatic
        fun delegateResults(): Stream<String?> {
            return Stream.of("delegateResult", null)
        }
    }

    @Test
    fun `when delegate throw error then interceptor write null result & rethrow error`() {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            @SingleResultVariable("result")
            fun test(): String = throw IllegalArgumentException()
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = SingleResultExecutionWriter().also {
            it.nextExecutor = DelegateExecutorImpl()
        }

        assertThrows<IllegalArgumentException> {
            interceptor.execute(execution, delegateInformation, emptyArray())
        }

        verify(execution).setVariableLocal(eq("result"), isNull())
    }

    @ParameterizedTest
    @MethodSource("delegateResults")
    fun `when delegate return local variable then write in local context`(
        delegateResult: String?
    ) {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            @SingleResultVariable("result")
            fun test(): String? = delegateResult
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = SingleResultExecutionWriter().also {
            it.nextExecutor = DelegateExecutorImpl()
        }

        interceptor.execute(execution, delegateInformation, emptyArray())

        verify(execution).setVariableLocal(eq("result"), eq(delegateResult))
    }

    @ParameterizedTest
    @MethodSource("delegateResults")
    fun `when delegate return global variable then write in global context`(
        delegateResult: String?
    ) {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            @SingleResultVariable("result", isLocal = false)
            fun test(): String? = delegateResult
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = SingleResultExecutionWriter().also {
            it.nextExecutor = DelegateExecutorImpl()
        }

        interceptor.execute(execution, delegateInformation, emptyArray())

        verify(execution).setVariable(eq("result"), eq(delegateResult))
    }

    @Test
    fun `when delegate return type is void then context never update`() {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            @SingleResultVariable("result")
            fun test() = Unit
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = SingleResultExecutionWriter().also {
            it.nextExecutor = DelegateExecutorImpl()
        }

        interceptor.execute(execution, delegateInformation, emptyArray())

        verify(execution, never()).setVariableLocal(any(), any())
        verify(execution, never()).setVariable(any(), any())
    }
}
