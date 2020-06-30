package ru.tinkoff.top.camunda.delegator.delegates.resolvers

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.MethodParameter
import org.springframework.validation.DataBinder
import ru.tinkoff.top.camunda.delegator.annotations.Variable
import java.lang.IllegalArgumentException

class ContextVariableResolver : DelegateArgumentResolver {
    override fun supportsParameter(
        methodParameter: MethodParameter
    ): Boolean {
        return methodParameter.getAnnotation<Variable>() != null
    }

    override fun resolveArgument(
        delegateExecution: DelegateExecution,
        methodParameter: MethodParameter
    ): Any? {
        val ann = methodParameter.getAnnotation<Variable>() ?: throw IllegalArgumentException()
        val parameterName = getParameterName(methodParameter, ann)
        val variable = if (ann.isLocal) {
            delegateExecution.getVariableLocal(parameterName)
        } else {
            delegateExecution.getVariable(parameterName)
        } ?: return null
        val binder = DataBinder(null, parameterName)
        return binder.convertIfNecessary(variable, methodParameter.parameterType, methodParameter)
    }
}

fun getVariableAnnotation(
    methodParameter: MethodParameter
): Variable? = methodParameter.getAnnotation()

fun getParameterName(methodParameter: MethodParameter, annotation: Variable): String {
    return if (annotation.name.isBlank()) {
        methodParameter.parameterName
            ?: throw IllegalArgumentException("Not found parameter name: $methodParameter")
    } else {
        annotation.name
    }
}
