package ru.tinkoff.top.camunda.delegator.docs.descriptors

import io.swagger.v3.oas.models.Components
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.docs.model.InputVariableInfo

interface DelegateInputDescriptor {

    fun describeInputVariable(
        components: Components,
        methodParameter: MethodParameter
    ): InputVariableInfo?
}
