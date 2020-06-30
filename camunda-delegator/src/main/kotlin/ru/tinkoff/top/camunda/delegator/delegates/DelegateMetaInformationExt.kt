package ru.tinkoff.top.camunda.delegator.delegates

import org.springframework.core.annotation.AnnotatedElementUtils

inline fun <reified T : Annotation> DelegateMetaInformation.getClassAnnotation(): T? {
    return AnnotatedElementUtils.findMergedAnnotation(
        delegateClass,
        T::class.java
    )
}

inline fun <reified T : Annotation> DelegateMetaInformation.getMethodAnnotation(): T? {
    return AnnotatedElementUtils.findMergedAnnotation(
        executionDelegateMethod,
        T::class.java
    )
}
