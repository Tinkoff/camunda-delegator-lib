package ru.tinkoff.top.camunda.delegator.docs.descriptors.input

import io.swagger.v3.oas.models.Components
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getParameterName
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getVariableAnnotation
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateInputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getSchema
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getVariableDescription
import ru.tinkoff.top.camunda.delegator.docs.model.InputVariableInfo

class CamundaContextVariableDescriptor : DelegateInputDescriptor {

    override fun describeInputVariable(
        components: Components,
        methodParameter: MethodParameter
    ): InputVariableInfo? {
        val ann = getVariableAnnotation(methodParameter) ?: return null
        val description = getVariableDescription(methodParameter.parameter)
        return InputVariableInfo(
            name = getParameterName(methodParameter, ann),
            isRequired = !methodParameter.isOptional,
            isLocal = ann.isLocal,
            isSystem = ann.isSystem,
            simpleClassName = methodParameter.genericParameterType.typeName,
            schema = getSchema(components, methodParameter),
            description = description
        )
    }
}
