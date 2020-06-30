package ru.tinkoff.top.camunda.delegator.delegates.resolvers.tenant

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getAnnotation

class TenantIdResolver : DelegateArgumentResolver {

    override fun supportsParameter(methodParameter: MethodParameter): Boolean {
        return methodParameter.getAnnotation<TenantId>() != null
    }

    override fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): String? {
        return delegateExecution.tenantId
    }
}
