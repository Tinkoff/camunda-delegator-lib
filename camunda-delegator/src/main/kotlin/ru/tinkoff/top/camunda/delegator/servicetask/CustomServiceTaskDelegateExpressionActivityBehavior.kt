package ru.tinkoff.top.camunda.delegator.servicetask

import org.camunda.bpm.engine.delegate.Expression
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.impl.bpmn.behavior.ServiceTaskDelegateExpressionActivityBehavior
import org.camunda.bpm.engine.impl.bpmn.delegate.ActivityBehaviorInvocation
import org.camunda.bpm.engine.impl.bpmn.delegate.JavaDelegateInvocation
import org.camunda.bpm.engine.impl.bpmn.parser.FieldDeclaration
import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution
import org.camunda.bpm.engine.impl.util.ClassDelegateUtil
import ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateMethodHandlerInvocation
import java.util.concurrent.Callable

/**
 *
 * DelegateExpressionExecutionListener for {@link MethodHandlerDelegate}
 *
 * @see MethodHandlerDelegate
 *
 * @author p.pletnev
 */
class CustomServiceTaskDelegateExpressionActivityBehavior(
    expression: Expression,
    private val fieldDeclarations: MutableList<FieldDeclaration>
) : ServiceTaskDelegateExpressionActivityBehavior(expression, fieldDeclarations) {

    override fun performExecution(execution: ActivityExecution) {
        val callable: Callable<Void?> = Callable {
            // Note: we can't cache the result of the expression, because the
            // execution can change: eg. delegateExpression='${mySpringBeanFactory.randomSpringBean()}'
            val delegate = expression.getValue(execution)
            ClassDelegateUtil.applyFieldDeclaration(fieldDeclarations, delegate)
            when (delegate) {
                is ActivityBehavior -> {
                    Context.getProcessEngineConfiguration()
                        .delegateInterceptor
                        .handleInvocation(ActivityBehaviorInvocation(delegate, execution))
                }
                is JavaDelegate -> {
                    Context.getProcessEngineConfiguration()
                        .delegateInterceptor
                        .handleInvocation(JavaDelegateInvocation(delegate, execution))
                    leave(execution)
                }
                is MethodHandlerDelegate -> {
                    Context.getProcessEngineConfiguration()
                        .delegateInterceptor
                        .handleInvocation(JavaDelegateMethodHandlerInvocation(delegate, execution))
                    leave(execution)
                }
                else -> {
                    throw LOG.resolveDelegateExpressionException(
                        expression,
                        ActivityBehavior::class.java,
                        JavaDelegate::class.java
                    )
                }
            }
            return@Callable null
        }
        executeWithErrorPropagation(execution, callable)
    }

    override fun getActivityBehaviorInstance(
        execution: ActivityExecution?,
        delegateInstance: Any
    ): ActivityBehavior? {
        return if (delegateInstance is MethodHandlerDelegate) {
            return ServiceTaskJavaDelegateMethodHandlerActivityBehavior(delegateInstance)
        } else {
            super.getActivityBehaviorInstance(execution, delegateInstance)
        }
    }
}
