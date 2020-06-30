package ru.tinkoff.top.camunda.delegator.config

import io.kotest.matchers.shouldBe
import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory
import org.camunda.bpm.engine.impl.el.ExpressionManager
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class BpmnParserCamundaPluginTest {

    private val expressionManager: ExpressionManager = mock()
    private val bpmnParseFactory: BpmnParseFactory = mock()

    @Test
    fun `when pre init configuration call then exp manager & parse factory replaced`() {
        val plugin = BpmnParserCamundaPlugin(expressionManager, bpmnParseFactory)
        val configuration = SpringProcessEngineBpmnParseConfiguration()
        plugin.preInit(configuration)

        configuration.expressionManager shouldBe expressionManager
        configuration.getBpmnParseFactory() shouldBe bpmnParseFactory
    }
}
