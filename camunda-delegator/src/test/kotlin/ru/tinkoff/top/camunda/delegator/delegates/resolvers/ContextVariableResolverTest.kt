package ru.tinkoff.top.camunda.delegator.delegates.resolvers

import io.kotest.matchers.shouldBe
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.annotations.Variable
import kotlin.reflect.jvm.javaMethod

@Suppress("UnusedPrivateMember")
class ContextVariableResolverTest {

    private val context = mock<DelegateExecution>()

    @Test
    fun `when method param has not annotation @Variable then resolver return non support`() {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            fun test(
                param: String
            ) = Unit
        }

        val resolver = ContextVariableResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0)
        assertFalse(resolver.supportsParameter(parameter))
    }

    @Test
    fun `when method param has annotation @Variable then resolver return value`() {
        whenever(context.getVariableLocal(eq("param"))).thenReturn("paramValue")

        @CamundaDelegate
        class TestDelegate {
            @DelegateExecute
            fun test(
                @Variable
                param: String
            ) = Unit
        }

        val resolver = ContextVariableResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0).also {
            it.initParameterNameDiscovery(DefaultParameterNameDiscoverer())
        }
        assertTrue(resolver.supportsParameter(parameter))

        val argument = resolver.resolveArgument(context, parameter)

        argument shouldBe "paramValue"
        verify(context).getVariableLocal(eq("param"))
        verify(context, never()).getVariable(any())
    }

    @Test
    fun `when method param has annotation @Variable with override name then resolver return value`() {
        whenever(context.getVariableLocal(eq("paramOverride"))).thenReturn("paramValue")

        @CamundaDelegate
        class TestDelegate {
            @DelegateExecute
            fun test(
                @Variable("paramOverride")
                param: String
            ) = Unit
        }

        val resolver = ContextVariableResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0).also {
            it.initParameterNameDiscovery(DefaultParameterNameDiscoverer())
        }
        assertTrue(resolver.supportsParameter(parameter))

        val argument = resolver.resolveArgument(context, parameter)

        argument shouldBe "paramValue"
        verify(context).getVariableLocal(eq("paramOverride"))
        verify(context, never()).getVariable(any())
    }

    @Test
    fun `when method param has annotation @Variable with override name & global context then resolver return value`() {
        whenever(context.getVariable(eq("paramOverride"))).thenReturn("paramValue")

        @CamundaDelegate
        class TestDelegate {
            @DelegateExecute
            fun test(
                @Variable("paramOverride", isLocal = false)
                param: String
            ) = Unit
        }

        val resolver = ContextVariableResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0).also {
            it.initParameterNameDiscovery(DefaultParameterNameDiscoverer())
        }
        assertTrue(resolver.supportsParameter(parameter))

        val argument = resolver.resolveArgument(context, parameter)

        argument shouldBe "paramValue"
        verify(context).getVariable(eq("paramOverride"))
        verify(context, never()).getVariableLocal(any())
    }

    @Test
    fun `when method param has annotation @Variable with global context then resolver return value`() {
        whenever(context.getVariable(eq("param"))).thenReturn("paramValue")

        @CamundaDelegate
        class TestDelegate {
            @DelegateExecute
            fun test(
                @Variable(isLocal = false)
                param: String
            ) = Unit
        }

        val resolver = ContextVariableResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0).also {
            it.initParameterNameDiscovery(DefaultParameterNameDiscoverer())
        }
        assertTrue(resolver.supportsParameter(parameter))

        val argument = resolver.resolveArgument(context, parameter)

        argument shouldBe "paramValue"
        verify(context).getVariable(eq("param"))
        verify(context, never()).getVariableLocal(any())
    }

    @Test
    fun `when method param has annotation @Variable with global context then resolver return null`() {
        whenever(context.getVariableLocal(eq("param"))).thenReturn(null)

        @CamundaDelegate
        class TestDelegate {
            @DelegateExecute
            fun test(
                @Variable
                param: String
            ) = Unit
        }

        val resolver = ContextVariableResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0).also {
            it.initParameterNameDiscovery(DefaultParameterNameDiscoverer())
        }
        assertTrue(resolver.supportsParameter(parameter))

        val argument = resolver.resolveArgument(context, parameter)

        argument shouldBe null
        verify(context).getVariableLocal(eq("param"))
        verify(context, never()).getVariable(any())
    }
}
