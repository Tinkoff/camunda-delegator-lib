package ru.tinkoff.top.camunda.delegator.docs.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DelegateInfo(
    val title: String = ""
)

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class VariableInfo(
    val description: String = "",
    val example: String = ""
)
