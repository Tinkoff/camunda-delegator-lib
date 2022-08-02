@file:Suppress("ktlint:filename")

package ru.tinkoff.top.camunda.delegator.docs.descriptors

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.SpringDocAnnotationsUtils
import org.springframework.core.MethodParameter
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

fun getSchema(components: Components, methodParameter: MethodParameter): Schema<*> {
    if (methodParameter.genericParameterType is ParameterizedType) {
        return SpringDocAnnotationsUtils.extractSchema(
            components,
            methodParameter.genericParameterType,
            null,
            methodParameter.parameterAnnotations
        )
    }
    return SpringDocAnnotationsUtils.resolveSchemaFromType(
        methodParameter.parameterType,
        components,
        null,
        methodParameter.parameterAnnotations
    )
}

fun getSchema(components: Components, property: KProperty<*>): Schema<*> {
    if (property.returnType.javaType is ParameterizedType) {
        return SpringDocAnnotationsUtils.extractSchema(
            components,
            property.returnType.javaType,
            null,
            property.annotations.toTypedArray()
        )
    }
    return SpringDocAnnotationsUtils.resolveSchemaFromType(
        property.returnType.jvmErasure.java,
        components,
        null,
        property.annotations.toTypedArray()
    )
}
