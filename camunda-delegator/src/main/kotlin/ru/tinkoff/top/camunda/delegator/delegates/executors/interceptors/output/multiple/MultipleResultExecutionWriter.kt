package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.MultipleResults
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.ResultVariable
import java.lang.RuntimeException
import java.lang.reflect.Method
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Interceptor processing delegate result and write variables to context.
 * To activate this interceptor mark delegate method with annotation @MultipleResults
 * @see MultipleResults
 * @see ResultVariable
 *
 * @author p.pletnev
 */
class MultipleResultExecutionWriter : DelegateInterceptor() {

    companion object : KLogging()

    override fun execute(
        execution: DelegateExecution,
        delegateInfo: DelegateInformation,
        delegateArguments: Array<Any?>?
    ): Any? {
        try {
            val result = nextExecutor.execute(execution, delegateInfo, delegateArguments)
            processReturnValue(delegateInfo.metaInformation.executionDelegateMethod, execution, result)
            return result
        } catch (ex: RuntimeException) {
            writeNullResults(delegateInfo.metaInformation.executionDelegateMethod, execution)
            throw ex
        }
    }

    @Suppress("ReturnCount")
    private fun delegateSupportedMultipleResult(
        delegateMethod: Method
    ): Boolean {
        AnnotatedElementUtils.findMergedAnnotation(delegateMethod, MultipleResults::class.java)
            ?: return false
        if (delegateMethod.returnType == Void.TYPE) {
            logger.warn { "Unsupported ${delegateMethod.returnType} return type for processor MultipleResults" }
            return false
        }
        return true
    }

    private fun processReturnValue(
        delegateMethod: Method,
        execution: DelegateExecution,
        result: Any?
    ) {
        if (!delegateSupportedMultipleResult(delegateMethod) || result == null) {
            return
        }

        result::class.memberProperties.forEach { p ->
            val info = p.findAnnotation<ResultVariable>() ?: return@forEach
            val variable = p.getter.call(result)
            val paramName = getParameterName(p, info)
            if (info.isLocal) {
                execution.setVariableLocal(paramName, variable)
            } else {
                execution.setVariable(paramName, variable)
            }
        }
    }

    private fun writeNullResults(
        delegateMethod: Method,
        execution: DelegateExecution
    ) {
        if (!delegateSupportedMultipleResult(delegateMethod)) {
            return
        }

        delegateMethod.returnType.kotlin.memberProperties.forEach { p ->
            val info = p.findAnnotation<ResultVariable>() ?: return@forEach
            val paramName = getParameterName(p, info)
            if (info.isLocal) {
                execution.setVariableLocal(paramName, null)
            } else {
                execution.setVariable(paramName, null)
            }
        }
    }
}

fun getParameterName(
    property: KProperty1<*, *>,
    resultVariable: ResultVariable
): String {
    return resultVariable.name.ifBlank {
        property.name
    }
}
