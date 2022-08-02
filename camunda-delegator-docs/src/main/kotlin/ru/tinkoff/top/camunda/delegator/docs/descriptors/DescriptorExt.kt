@file:Suppress("ktlint:filename")

package ru.tinkoff.top.camunda.delegator.docs.descriptors

import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.docs.annotations.VariableInfo
import java.lang.reflect.AnnotatedElement
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

fun getVariableDescription(element: AnnotatedElement): String? {
    val infAnn = AnnotatedElementUtils.findMergedAnnotation(element, VariableInfo::class.java)
        ?: return null
    return infAnn.description.ifBlank {
        null
    }
}

fun getVariableDescription(property: KProperty<*>): String? {
    val infAnn = property.findAnnotation<VariableInfo>()
        ?: return null
    return infAnn.description.ifBlank {
        null
    }
}
