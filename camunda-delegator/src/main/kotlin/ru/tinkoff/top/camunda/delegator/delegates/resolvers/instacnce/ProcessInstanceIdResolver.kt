package ru.tinkoff.top.camunda.delegator.delegates.resolvers.instacnce

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getAnnotation

class ProcessInstanceIdResolver : DelegateArgumentResolver {

    override fun supportsParameter(methodParameter: MethodParameter): Boolean {
        return methodParameter.getAnnotation<ProcessInstanceId>() != null
    }

    override fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): String? {
        return delegateExecution.processInstanceId
    }
}
