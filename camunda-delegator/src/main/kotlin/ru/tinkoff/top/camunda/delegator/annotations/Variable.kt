package ru.tinkoff.top.camunda.delegator.annotations

/**
 * Annotation for method parameter from camunda context
 *
 * @param name - name of variable
 * @param isLocal - context of variable
 * @param isSystem - variable that should be passed to delegate automatically
 *
 * @see ru.tinkoff.top.camunda.delegator.delegates.resolvers.ContextVariableResolver
 *
 * @author p.pletnev
 * */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Variable(
    val name: String = "",
    val isLocal: Boolean = true,
    val isSystem: Boolean = false
)
