package ru.tinkoff.top.camunda.delegator.docs.descriptors.output

import io.swagger.v3.oas.models.Components
import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultVariable
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateOutputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getSchema
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getVariableDescription
import ru.tinkoff.top.camunda.delegator.docs.model.OutputVariableInfo

class SingleResultOutputDescriptor : DelegateOutputDescriptor {

    override fun describeResultValues(
        components: Components,
        metaInformation: DelegateMetaInformation
    ): List<OutputVariableInfo> {
        if (metaInformation.returnType == Void.TYPE) {
            return emptyList()
        }

        val executionMethod = metaInformation.executionDelegateMethod
        val returnParameter = metaInformation.returnParameter

        val resultVariable =
            AnnotatedElementUtils.findMergedAnnotation(executionMethod, SingleResultVariable::class.java)
                ?: return emptyList()
        val outputVariableInfo = OutputVariableInfo(
            name = resultVariable.name,
            isRequired = !returnParameter.isOptional,
            isLocal = resultVariable.isLocal,
            description = getVariableDescription(executionMethod),
            simpleClassName = returnParameter.genericParameterType.typeName,
            schema = getSchema(components, returnParameter)
        )
        return listOf(outputVariableInfo)
    }
}
