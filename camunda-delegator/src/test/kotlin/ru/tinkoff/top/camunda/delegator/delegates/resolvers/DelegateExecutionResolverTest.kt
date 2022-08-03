package ru.tinkoff.top.camunda.delegator.delegates.resolvers

import io.kotest.matchers.shouldBe
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import kotlin.reflect.jvm.javaMethod

class DelegateExecutionResolverTest {

    @Test
    fun `when method param is not delegateExecution then resolver return non support`() {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            fun test(
                param: String
            ) = Unit
        }

        val resolver = DelegateExecutionResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0)
        resolver.supportsParameter(parameter) shouldBe false
    }

    @Test
    fun `when method param is delegateExecution then resolver return him`() {
        @CamundaDelegate
        class TestDelegate {

            @DelegateExecute
            fun test(
                @DelegateExecutionAnn delegateExecution: DelegateExecution
            ) = Unit
        }

        val resolver = DelegateExecutionResolver()

        val parameter = MethodParameter(TestDelegate::test.javaMethod!!, 0)
        resolver.supportsParameter(parameter) shouldBe true

        val context = mock<DelegateExecution>()
        val resolvedArgument = resolver.resolveArgument(context, parameter)

        resolvedArgument shouldBe context
    }
}
