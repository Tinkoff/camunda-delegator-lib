package ru.tinkoff.top.camunda.delegator.docs.descriptors.output

import io.swagger.v3.oas.models.Components
import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.MultipleResults
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.ResultVariable
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.getParameterName
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateOutputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getSchema
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getVariableDescription
import ru.tinkoff.top.camunda.delegator.docs.model.OutputVariableInfo
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

class MultipleResultOutputDescriptor : DelegateOutputDescriptor {

    override fun describeResultValues(
        components: Components,
        metaInformation: DelegateMetaInformation
    ): List<OutputVariableInfo> {
        if (metaInformation.returnType == Void.TYPE) {
            return emptyList()
        }
        AnnotatedElementUtils.findMergedAnnotation(metaInformation.executionDelegateMethod, MultipleResults::class.java)
            ?: return emptyList()

        val resultClass = metaInformation.returnType.kotlin

        return resultClass.memberProperties.mapNotNull { p ->
            val info = p.findAnnotation<ResultVariable>() ?: return@mapNotNull null
            val paramName = getParameterName(p, info)

            OutputVariableInfo(
                name = paramName,
                isRequired = !p.returnType.isMarkedNullable,
                isLocal = info.isLocal,
                description = getVariableDescription(p),
                simpleClassName = p.returnType.javaType.typeName,
                schema = getSchema(components, p)
            )
        }
    }
}
