package ru.tinkoff.top.camunda.delegator.test

import io.kotest.matchers.nulls.shouldNotBeNull
import org.camunda.bpm.engine.test.mock.Mocks
import org.junit.jupiter.api.Test
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutorImpl

class DelegateMethodHandlerMockRegisterTest {

    private val register = DelegateMethodHandlerMockRegister(
        listOf(), DelegateExecutorImpl()
    )

    @Test
    fun `when delegate is register then present in Mocks`() {
        register.initDelegates(listOf(DemoDelegate()))

        Mocks.get("demoDelegateAlias").shouldNotBeNull()
        Mocks.get("demoDelegate_generateProductPriority").shouldNotBeNull()
    }
}
