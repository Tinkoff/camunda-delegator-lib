package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.exception

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor
import java.lang.reflect.Method

/**
 * Interceptor for processing delegate exception. Exception can be rethrown or logging here.
 *
 * @see ExceptionProcessingStrategy
* */
class DelegateExceptionProcessingInterceptor : DelegateInterceptor() {

    companion object : KLogging()

    override fun execute(
        execution: DelegateExecution,
        delegateInfo: DelegateInformation,
        delegateArguments: Array<Any?>?
    ): Any? {
        return try {
            nextExecutor.execute(execution, delegateInfo, delegateArguments)
        } catch (ex: Exception) {
            when (getExceptionProcessingStrategy(delegateInfo.metaInformation.executionDelegateMethod)) {
                ExceptionProcessingStrategy.FORWARD -> {
                    throw ex
                }
                ExceptionProcessingStrategy.LOG -> {
                    logger.warn("Skip delegate error", ex)
                    null
                }
            }
        }
    }

    private fun getExceptionProcessingStrategy(
        executionMethod: Method
    ): ExceptionProcessingStrategy {
        return AnnotatedElementUtils.findMergedAnnotation(
            executionMethod,
            ExceptionStrategy::class.java
        )?.exceptionProcessingStrategy ?: ExceptionProcessingStrategy.FORWARD
    }
}
