package ru.tinkoff.top.camunda.delegator.delegates.executors

import org.camunda.bpm.engine.delegate.DelegateExecution
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation

/**
 * The delegate executor
 *
 * @author p.pletnev
 * */
interface DelegateExecutor {

    /**
     * @param execution - is DelegateExecution
     * @param delegateInfo - is delegate information, such as meta information
     * @param delegateArguments - method arguments should be passed to delegate method
     * */
    fun execute(
        execution: DelegateExecution,
        delegateInfo: DelegateInformation,
        delegateArguments: Array<Any?>?
    ): Any?
}
