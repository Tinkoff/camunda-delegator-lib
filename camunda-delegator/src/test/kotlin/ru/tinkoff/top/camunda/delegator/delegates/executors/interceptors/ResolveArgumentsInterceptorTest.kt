package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.string.shouldStartWith
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutorImpl
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver
import java.lang.reflect.Method
import java.util.stream.Stream
import kotlin.reflect.jvm.javaMethod

@Suppress("UnusedPrivateMember")
class ResolveArgumentsInterceptorTest {

    companion object {

        @CamundaDelegate
        class TestDelegateWithParams {

            @DelegateExecute
            fun testRequiredParam(test: String) = Unit

            @DelegateExecute
            fun testOptionalParam(test: String?) = Unit
        }

        @JvmStatic
        fun delegatesWithRequiredParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    TestDelegateWithParams(),
                    TestDelegateWithParams::class.java,
                    TestDelegateWithParams::testRequiredParam.javaMethod!!
                ),
                Arguments.of(
                    JavaDelegateWithParams(),
                    JavaDelegateWithParams::class.java,
                    JavaDelegateWithParams::requiredParams.javaMethod!!
                )
            )
        }

        @JvmStatic
        fun delegatesWithOptionalParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    TestDelegateWithParams(),
                    TestDelegateWithParams::class.java,
                    TestDelegateWithParams::testOptionalParam.javaMethod!!
                ),
                Arguments.of(
                    ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams(),
                    ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams::class.java,
                    ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams::optionalParams.javaMethod!!
                )
            )
        }
    }

    private val execution = mock<DelegateExecution>()
    private val executor = spy(DelegateExecutorImpl())
    private val argumentResolver = mock<DelegateArgumentResolver>()

    @Test
    fun `when delegate has not params then call with empty array`() {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            fun test() = Unit
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )

        val interceptor = ResolveArgumentsInterceptor(emptyList()).also {
            it.nextExecutor = executor
        }

        interceptor.execute(execution, delegateInformation, null)

        verify(executor).execute(
            any(),
            any(),
            check { it.shouldBeEmpty() }
        )
    }

    @ParameterizedTest
    @MethodSource("delegatesWithRequiredParams")
    fun `when not found required params then throw error`(
        delegate: Any,
        delegateClass: Class<*>,
        executionDelegateMethod: Method
    ) {
        whenever(argumentResolver.supportsParameter(any())).thenReturn(true)
        whenever(argumentResolver.resolveArgument(any(), any())).thenReturn(null)

        val delegateInformation = DelegateInformation(
            delegate, DelegateMetaInformation(delegateClass, executionDelegateMethod)
        )

        val interceptor = ResolveArgumentsInterceptor(emptyList()).also {
            it.nextExecutor = executor
        }

        val error = assertThrows<IllegalArgumentException> {
            interceptor.execute(execution, delegateInformation, null)
        }

        error.message shouldStartWith "Not found value for required params"
    }

    @ParameterizedTest
    @MethodSource("delegatesWithOptionalParams")
    fun `when not found optional params params then return null`(
        delegate: Any,
        delegateClass: Class<*>,
        executionDelegateMethod: Method
    ) {
        whenever(argumentResolver.supportsParameter(any())).thenReturn(true)
        whenever(argumentResolver.resolveArgument(any(), any())).thenReturn(null)

        val delegateInformation = DelegateInformation(
            delegate, DelegateMetaInformation(delegateClass, executionDelegateMethod)
        )

        val interceptor = ResolveArgumentsInterceptor(emptyList()).also {
            it.nextExecutor = executor
        }

        interceptor.execute(execution, delegateInformation, null)

        verify(executor).execute(
            any(),
            any(),
            check { arrayOfNulls<String>(1) }
        )
    }
}
