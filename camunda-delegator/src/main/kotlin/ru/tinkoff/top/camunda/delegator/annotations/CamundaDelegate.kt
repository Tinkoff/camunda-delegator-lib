package ru.tinkoff.top.camunda.delegator.annotations

import org.springframework.stereotype.Component

/**
 * Base annotation for marking class as Camunda delegate and Spring bean.
 *
 * By default, camunda delegate name prefix is class name,
 * but your can override this prefix by
 *
 * @param name
 *
 * @author p.pletnev
 * */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class CamundaDelegate(
    val name: String = ""
)

/**
 * Base annotation for marking method in Camunda delegate class as delegate executor.
 *
 * By default, camunda delegate name suffix is method name,
 * but your can override this prefix by
 *
 * @param version
 *
 * @author p.pletnev
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DelegateExecute(
    val version: String = ""
)

/**
 * Annotation for creating alias for delegate. Alias must be unique.
 *
 * @author p.pletnev
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DelegateAliases(
    vararg val values: String
)
