package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor
import java.lang.reflect.Method

/**
 * Interceptor processing delegate result and write variable to context.
 * To activate this interceptor mark delegate method with annotation @SingleResultVariable
 * @see SingleResultVariable
 *
 * @author p.pletnev
 */
class SingleResultExecutionWriter : DelegateInterceptor() {

    companion object : KLogging()

    override fun execute(
        execution: DelegateExecution,
        delegateInfo: DelegateInformation,
        delegateArguments: Array<Any?>?
    ): Any? {
        try {
            val result = nextExecutor.execute(execution, delegateInfo, delegateArguments)
            processReturnValue(
                delegateInfo.metaInformation.executionDelegateMethod,
                execution,
                result
            )
            return result
        } catch (ex: RuntimeException) {
            processReturnValue(
                delegateInfo.metaInformation.executionDelegateMethod,
                execution,
                null
            )
            throw ex
        }
    }

    private fun processReturnValue(
        delegateMethod: Method,
        execution: DelegateExecution,
        result: Any?
    ) {
        val resultVariable = AnnotatedElementUtils.findMergedAnnotation(
            delegateMethod,
            SingleResultVariable::class.java
        ) ?: return
        if (delegateMethod.returnType == Void.TYPE) {
            logger.warn { "Unsupported ${delegateMethod.returnType} return type for processor SingleResultProcessor" }
            return
        }
        if (resultVariable.isLocal) {
            execution.setVariableLocal(resultVariable.name, result)
        } else {
            execution.setVariable(resultVariable.name, result)
        }
    }
}
