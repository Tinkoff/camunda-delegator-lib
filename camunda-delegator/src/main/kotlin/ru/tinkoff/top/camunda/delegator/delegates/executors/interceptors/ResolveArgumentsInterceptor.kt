package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter
import org.springframework.util.ObjectUtils
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver

/**
 * Interceptor for resolving method arguments for delegate
 *
 * @param argumentResolvers - all arguments resolver
* */
class ResolveArgumentsInterceptor(
    private val argumentResolvers: List<DelegateArgumentResolver>
) : DelegateInterceptor() {

    companion object : KLogging() {
        val EMPTY_ARGS = emptyArray<Any?>()
    }

    override fun execute(
        execution: DelegateExecution,
        delegateInfo: DelegateInformation,
        delegateArguments: Array<Any?>?
    ): Any? {
        val resolvedArgs = extractArgsForMethod(execution, delegateInfo.metaInformation.methodParameters)
        return nextExecutor.execute(execution, delegateInfo, resolvedArgs)
    }

    private fun extractArgsForMethod(
        delegateExecution: DelegateExecution,
        methodParameters: Array<MethodParameter>
    ): Array<Any?> {
        if (ObjectUtils.isEmpty(methodParameters)) {
            return EMPTY_ARGS
        }
        return Array(methodParameters.size) {
            val parameter = methodParameters[it]
            var value: Any? = null
            argumentResolvers.map { extractor ->
                if (extractor.supportsParameter(parameter)) {
                    value = extractor.resolveArgument(delegateExecution, parameter)
                }
            }
            if (value == null && !parameter.isOptional) {
                throw IllegalArgumentException("Not found value for required params ${parameter.parameterName}")
            }
            return@Array value
        }
    }
}
