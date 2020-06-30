package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single

/**
 * Annotation that indicate delegate return one result
 *
 * @param name - is variable name
 * @param isLocal - is variable context
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleResultVariable(
    val name: String = "",
    val isLocal: Boolean = true
)
