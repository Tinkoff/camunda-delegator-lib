package ru.tinkoff.top.camunda.delegator.delegates.resolvers

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter

class DelegateExecutionResolver : DelegateArgumentResolver {

    override fun supportsParameter(methodParameter: MethodParameter): Boolean {
        return methodParameter.getAnnotation<DelegateExecutionAnn>() != null &&
            DelegateExecution::class.java.isAssignableFrom(methodParameter.parameterType)
    }

    override fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): Any {
        return delegateExecution
    }
}
