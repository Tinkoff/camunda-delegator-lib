@file:Suppress("ktlint:filename")

package ru.tinkoff.top.camunda.delegator.delegates.resolvers

import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils

inline fun <reified T : Annotation> MethodParameter.getAnnotation(): T? =
    AnnotatedElementUtils.findMergedAnnotation(this.parameter, T::class.java)
