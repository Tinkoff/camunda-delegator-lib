package ru.tinkoff.top.camunda.delegator.delegates.executors

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.annotations.Variable
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import kotlin.reflect.jvm.javaMethod

class DelegateExecutorImplTest {

    @Test
    fun `when method arguments not present then error`() {
        @CamundaDelegate
        class TestDelegate {
            @DelegateExecute
            fun test(
                @Variable
                param: String
            ) = Unit
        }

        val delegateInformation = DelegateInformation(
            TestDelegate(),
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )
        val delegate = DelegateExecutorImpl()
        val error = assertThrows<IllegalArgumentException> {
            delegate.execute(mock(), delegateInformation, null)
        }
        error.message shouldBe "For executor arguments not resolved"
    }

    @Test
    fun `when method arguments present then delegate method call`() {
        @CamundaDelegate
        class TestDelegate {
            @DelegateExecute
            fun test() = Unit
        }

        val delegate = spy(TestDelegate())

        val delegateInformation = DelegateInformation(
            delegate,
            DelegateMetaInformation(TestDelegate::class.java, TestDelegate::test.javaMethod!!)
        )
        val delegateExecutor = DelegateExecutorImpl()
        delegateExecutor.execute(mock(), delegateInformation, emptyArray())

        verify(delegate).test()
    }
}
