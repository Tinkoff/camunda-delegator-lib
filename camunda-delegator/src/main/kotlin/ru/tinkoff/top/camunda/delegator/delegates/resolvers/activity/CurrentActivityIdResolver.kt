package ru.tinkoff.top.camunda.delegator.delegates.resolvers.activity

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getAnnotation

class CurrentActivityIdResolver : DelegateArgumentResolver {

    override fun supportsParameter(methodParameter: MethodParameter): Boolean {
        return methodParameter.getAnnotation<CurrentActivityId>() != null
    }

    override fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): String? {
        return delegateExecution.currentActivityId
    }
}
