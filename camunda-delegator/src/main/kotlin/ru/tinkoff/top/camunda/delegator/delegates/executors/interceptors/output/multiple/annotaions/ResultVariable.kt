package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions

/**
 * DON`T USE VALUE_PARAMETER IN TARGET
 * */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResultVariable(
    val name: String = "",
    val isLocal: Boolean = true
)
