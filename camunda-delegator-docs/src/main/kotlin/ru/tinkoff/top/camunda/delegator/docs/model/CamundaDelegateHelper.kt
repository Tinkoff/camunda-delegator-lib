package ru.tinkoff.top.camunda.delegator.docs.model

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("")
annotation class CamundaDelegateHelper(
    val title: String = ""
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("")
annotation class HelperMethod(
    val title: String = ""
)
