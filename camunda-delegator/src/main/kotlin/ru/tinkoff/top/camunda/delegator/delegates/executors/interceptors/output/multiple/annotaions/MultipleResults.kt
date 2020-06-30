package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions

/**
 * Marker annotation that indicate delegate return multiple results
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MultipleResults
