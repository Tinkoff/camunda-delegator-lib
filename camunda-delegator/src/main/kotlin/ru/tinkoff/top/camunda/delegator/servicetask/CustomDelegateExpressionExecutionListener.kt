package ru.tinkoff.top.camunda.delegator.servicetask

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.Expression
import org.camunda.bpm.engine.impl.bpmn.listener.DelegateExpressionExecutionListener
import org.camunda.bpm.engine.impl.bpmn.parser.FieldDeclaration
import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.engine.impl.util.ClassDelegateUtil
import ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateMethodHandlerInvocation

/**
 *
 * DelegateExpressionExecutionListener for {@link MethodHandlerDelegate}
 *
 * @see MethodHandlerDelegate
 *
 * @author p.pletnev
 */
class CustomDelegateExpressionExecutionListener(
    expression: Expression,
    private val fieldDeclarations: MutableList<FieldDeclaration>
) : DelegateExpressionExecutionListener(expression, fieldDeclarations) {

    override fun notify(execution: DelegateExecution) {
        // Note: we can't cache the result of the expression, because the
        // execution can change: eg. delegateExpression='${mySpringBeanFactory.randomSpringBean()}'
        val delegate = expression.getValue(execution)
        if (delegate is MethodHandlerDelegate) {
            ClassDelegateUtil.applyFieldDeclaration(fieldDeclarations, delegate)
            Context.getProcessEngineConfiguration()
                .delegateInterceptor
                .handleInvocation(JavaDelegateMethodHandlerInvocation(delegate, execution))
        } else {
            super.notify(execution)
        }
    }
}
