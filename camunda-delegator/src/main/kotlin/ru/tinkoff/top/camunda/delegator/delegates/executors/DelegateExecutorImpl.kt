package ru.tinkoff.top.camunda.delegator.delegates.executors

import org.camunda.bpm.engine.delegate.DelegateExecution
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import java.lang.reflect.InvocationTargetException

/**
 * Default delegate executor
 *
 * @author p.pletnev
 * */
open class DelegateExecutorImpl : DelegateExecutor {

    override fun execute(
        execution: DelegateExecution,
        delegateInfo: DelegateInformation,
        delegateArguments: Array<Any?>?
    ): Any? {
        if (delegateArguments == null) {
            throw IllegalArgumentException("For executor arguments not resolved")
        }
        try {
            val executionMethod = delegateInfo.metaInformation.executionDelegateMethod
            return executionMethod.invoke(delegateInfo.delegateBean, *delegateArguments)
        } catch (ex: InvocationTargetException) {
            throw ex.targetException
        }
    }
}
