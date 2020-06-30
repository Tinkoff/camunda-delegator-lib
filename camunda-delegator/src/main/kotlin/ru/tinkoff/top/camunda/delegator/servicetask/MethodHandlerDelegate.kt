package ru.tinkoff.top.camunda.delegator.servicetask

import org.camunda.bpm.engine.delegate.DelegateExecution
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation

/**
 * Convience class that should be used when a Java delegation in a BPMN 2.0
 * process is required.
 *
 * This class provide delegate information, such as delegate instance and meta information
 *
 * This class can be used for both service tasks and event listeners.
 *
 * @author p.pletnev
 */
interface MethodHandlerDelegate {
    val delegateInformation: DelegateInformation
    fun execute(execution: DelegateExecution)
}
