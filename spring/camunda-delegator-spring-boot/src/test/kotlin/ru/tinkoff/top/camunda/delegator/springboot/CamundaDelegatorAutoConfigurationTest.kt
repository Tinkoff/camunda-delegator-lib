package ru.tinkoff.top.camunda.delegator.springboot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import ru.tinkoff.top.camunda.delegator.config.DelegatorBpmnParserCamundaPlugin

class CamundaDelegatorAutoConfigurationTest {

    private val contextRunner = ApplicationContextRunner()
        .withUserConfiguration(CamundaDelegatorAutoConfiguration::class.java)

    @Test
    fun `check that delegator camunda plugin creating by default`() {
        contextRunner
            .run {
                assertThat(it).hasNotFailed()
                assertThat(it).hasSingleBean(DelegatorBpmnParserCamundaPlugin::class.java)
            }
    }

    @Test
    fun `when bpmn parser is not enabled then camunda plugin is not created`() {
        contextRunner
            .withPropertyValues(
                "delegator.plugins.bpmn-parser.enabled=false"
            )
            .run {
                assertThat(it).hasNotFailed()
                assertThat(it).doesNotHaveBean(DelegatorBpmnParserCamundaPlugin::class.java)
            }
    }
}
