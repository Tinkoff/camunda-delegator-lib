package ru.tinkoff.top.camunda.delegator.process

import org.assertj.core.data.MapEntry
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests

fun ProcessInstance.hasPassed(vararg activity: String): ProcessInstance {
    BpmnAwareTests.assertThat(this).hasPassed(*activity)
    return this
}

fun ProcessInstance.contains(name: String, value: Any): ProcessInstance {
    BpmnAwareTests.assertThat(this).variables().contains(MapEntry.entry(name, value))
    return this
}
