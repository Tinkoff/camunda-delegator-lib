package ru.tinkoff.top.camunda.delegator.process

import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory
import org.camunda.bpm.extension.process_test_coverage.junit.rules.ProcessCoverageInMemProcessEngineConfiguration

class TestCamundaConfiguration : ProcessCoverageInMemProcessEngineConfiguration() {

    fun setBpmnParseFactory(bpmnParseFactory: BpmnParseFactory) {
        this.bpmnParseFactory = bpmnParseFactory
    }
}
