package ru.tinkoff.top.camunda.delegator.docs.descriptors.input

import io.swagger.v3.oas.models.Components
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateInputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.model.InputVariableInfo

class DelegateExecutionDescriptor : DelegateInputDescriptor {

    override fun describeInputVariable(
        components: Components,
        methodParameter: MethodParameter
    ): InputVariableInfo? {
        val parameterType = methodParameter.parameterType
        if (!DelegateExecution::class.java.isAssignableFrom(parameterType)) {
            return null
        }
        return InputVariableInfo(
            name = methodParameter.parameterName!!,
            isSystem = true,
            schema = null,
            simpleClassName = methodParameter.genericParameterType.typeName
        )
    }
}
