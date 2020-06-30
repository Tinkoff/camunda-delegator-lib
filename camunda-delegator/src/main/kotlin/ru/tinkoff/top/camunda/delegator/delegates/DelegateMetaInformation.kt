package ru.tinkoff.top.camunda.delegator.delegates

import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateAliases
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import java.lang.reflect.Method

const val RETURN_VALUE_INDEX_PARAM = -1

/**
 * Meta information for Camunda delegator instance
 *
 * @author p.pletnev
 * */
data class DelegateMetaInformation(
    val delegateClass: Class<*>,
    val executionDelegateMethod: Method
) {
    val delegateName: String = getDelegateName(delegateClass)
    val delegateVersion: String = getDelegateVersion(executionDelegateMethod)
    val delegateFullName: String = "${delegateName}_$delegateVersion"
    val delegateAliases: Set<String> = getDelegateAliases(executionDelegateMethod)

    val methodParameters: Array<MethodParameter> =
        Array(executionDelegateMethod.parameterCount) {
            val methodParameter = MethodParameter(executionDelegateMethod, it)
            methodParameter.initParameterNameDiscovery(DefaultParameterNameDiscoverer())
            methodParameter
        }
    val returnParameter = MethodParameter(executionDelegateMethod, RETURN_VALUE_INDEX_PARAM).also {
        it.initParameterNameDiscovery(DefaultParameterNameDiscoverer())
    }
    val returnType: Class<*> = executionDelegateMethod.returnType

    fun getShortDescription() =
        "${delegateClass.canonicalName}#${executionDelegateMethod.name}[${executionDelegateMethod.parameterCount}args]"
}

fun getDelegateName(delegateClass: Class<*>): String {
    val delegateAnn = AnnotatedElementUtils.findMergedAnnotation(
        delegateClass,
        CamundaDelegate::class.java
    )
        ?: throw IllegalArgumentException("Class $delegateClass is not marked as CamundaDelegate")
    return delegateAnn.name.ifBlank {
        delegateClass.simpleName.decapitalize()
    }
}

fun getDelegateVersion(method: Method): String {
    val delegateExMethod = AnnotatedElementUtils.findMergedAnnotation(
        method,
        DelegateExecute::class.java
    )
        ?: throw IllegalArgumentException("Method is not marked as DelegateExecute")
    return delegateExMethod.version.ifBlank {
        method.name
    }
}

fun getDelegateAliases(method: Method): Set<String> {
    return AnnotatedElementUtils.findMergedAnnotation(
        method,
        DelegateAliases::class.java
    )?.values?.toSet() ?: emptySet()
}
