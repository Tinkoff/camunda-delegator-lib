package ru.tinkoff.top.camunda.delegator.config

import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration

/**
 * Custom Spring camunda configuration file which provides accessors for field bpmnParseFactory.
 *
 * Only use for camunda version <= 7.13.0
 *
 * @author p.pletnev
 * */
open class SpringProcessEngineBpmnParseConfiguration : SpringProcessEngineConfiguration() {

    fun getBpmnParseFactory(): BpmnParseFactory {
        return bpmnParseFactory
    }

    fun setBpmnParseFactory(bpmnParseFactory: BpmnParseFactory) {
        this.bpmnParseFactory = bpmnParseFactory
    }
}
