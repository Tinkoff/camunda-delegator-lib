package ru.tinkoff.top.camunda.delegator.delegates.resolvers.bpmn

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getAnnotation

class BpmnModelInstanceResolver : DelegateArgumentResolver {

    override fun supportsParameter(methodParameter: MethodParameter): Boolean {
        return methodParameter.getAnnotation<BpmnModelInstanceAnnotation>() != null
    }

    override fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): BpmnModelInstance? {
        return delegateExecution.bpmnModelInstance
    }
}
