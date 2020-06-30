package ru.tinkoff.top.camunda.delegator.delegates.resolvers.bpmn

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.model.bpmn.instance.FlowElement
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getAnnotation

class FlowElementResolver : DelegateArgumentResolver {

    override fun supportsParameter(methodParameter: MethodParameter): Boolean {
        return methodParameter.getAnnotation<FlowElementAnnotation>() != null
    }

    override fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): FlowElement? {
        return delegateExecution.bpmnModelElementInstance
    }
}
