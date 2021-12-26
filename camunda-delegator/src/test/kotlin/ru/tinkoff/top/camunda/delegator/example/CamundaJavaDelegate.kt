package ru.tinkoff.top.camunda.delegator.example

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class CamundaJavaDelegate : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        execution.setVariable("javaDelegateVariableName", "javaDelegateVariableValue")
    }
}
