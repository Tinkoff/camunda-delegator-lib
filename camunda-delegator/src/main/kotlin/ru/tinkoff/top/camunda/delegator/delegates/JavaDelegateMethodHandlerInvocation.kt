package ru.tinkoff.top.camunda.delegator.delegates

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.impl.delegate.DelegateInvocation
import ru.tinkoff.top.camunda.delegator.servicetask.MethodHandlerDelegate

/**
 * Delegate invocation for {@link MethodHandlerDelegate}
 *
 * @see MethodHandlerDelegate
 *
 * @author p.pletnev
 */
class JavaDelegateMethodHandlerInvocation(
    private val methodHandlerDelegate: MethodHandlerDelegate,
    private val delegateExecution: DelegateExecution
) : DelegateInvocation(delegateExecution, null) {

    override fun invoke() {
        methodHandlerDelegate.execute(delegateExecution)
    }
}
