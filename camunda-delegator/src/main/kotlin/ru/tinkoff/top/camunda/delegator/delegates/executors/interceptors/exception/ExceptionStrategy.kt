package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.exception

/**
 * Annotation for indicate how to process errors by passed strategy
 *
 * @param exceptionProcessingStrategy
 *
 * */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExceptionStrategy(
    val exceptionProcessingStrategy: ExceptionProcessingStrategy
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ExceptionStrategy(ExceptionProcessingStrategy.FORWARD)
annotation class ForwardException

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ExceptionStrategy(ExceptionProcessingStrategy.LOG)
annotation class LogException
