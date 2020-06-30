package ru.tinkoff.top.camunda.delegator.delegates.resolvers

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter

/**
 * @author p.pletnev
 * */
interface DelegateArgumentResolver {

    fun supportsParameter(methodParameter: MethodParameter): Boolean

    fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): Any?
}
