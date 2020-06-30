package ru.tinkoff.top.camunda.delegator.servicetask

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.engine.impl.bpmn.behavior.TaskActivityBehavior
import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution
import ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateMethodHandlerInvocation

/**
 * @see JavaDelegateMethodHandlerInvocation
 *
 * @author p.pletnev
 */
class ServiceTaskJavaDelegateMethodHandlerActivityBehavior(
    private val methodHandlerDelegate: MethodHandlerDelegate
) : TaskActivityBehavior(), ActivityBehavior, ExecutionListener {

    override fun notify(execution: DelegateExecution) {
        execute(execution)
    }

    override fun performExecution(execution: ActivityExecution) {
        execute(execution as DelegateExecution)
        leave(execution)
    }

    private fun execute(execution: DelegateExecution) {
        Context.getProcessEngineConfiguration()
            .delegateInterceptor
            .handleInvocation(JavaDelegateMethodHandlerInvocation(methodHandlerDelegate, execution))
    }
}
