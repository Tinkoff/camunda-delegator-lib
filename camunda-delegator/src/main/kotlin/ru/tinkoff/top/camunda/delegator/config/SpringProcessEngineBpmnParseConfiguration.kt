package ru.tinkoff.top.camunda.delegator.config

import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration

/**
 * Custom Spring camunda configuration file which provides accessors for field bpmnParseFactory.
 *
 * Only use for camunda version <= 7.13.0
 *
 * @author p.pletnev
 * */
@Deprecated(
    "Since version 7.14, assessors for bpmnParseFactory have been added to the ProcessEngineConfigurationImpl",
    replaceWith = ReplaceWith(
        expression = "SpringProcessEngineConfiguration",
        imports = ["org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration"]
    )
)
open class SpringProcessEngineBpmnParseConfiguration : SpringProcessEngineConfiguration() {

    override fun getBpmnParseFactory(): BpmnParseFactory {
        return bpmnParseFactory
    }

    override fun setBpmnParseFactory(bpmnParseFactory: BpmnParseFactory): ProcessEngineConfigurationImpl {
        this.bpmnParseFactory = bpmnParseFactory
        return this
    }
}
