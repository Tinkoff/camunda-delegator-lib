package ru.tinkoff.top.camunda.delegator.servicetask

import org.camunda.bpm.engine.impl.bpmn.behavior.ClassDelegateActivityBehavior
import org.camunda.bpm.engine.impl.bpmn.parser.FieldDeclaration
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution
import org.camunda.bpm.engine.impl.util.ClassDelegateUtil

/**
 *
 * Helper class for supporting {@link MethodHandlerDelegate} activity behavior
 *
 * @see MethodHandlerDelegate
 *
 * @author p.pletnev
 */
class CustomClassDelegateActivityBehavior(
    className: String,
    fieldDeclarations: MutableList<FieldDeclaration>
) : ClassDelegateActivityBehavior(className, fieldDeclarations) {

    override fun getActivityBehaviorInstance(execution: ActivityExecution?): ActivityBehavior? {
        val delegateInstance = ClassDelegateUtil.instantiateDelegate(className, fieldDeclarations)
        return if (delegateInstance is MethodHandlerDelegate) {
            ServiceTaskJavaDelegateMethodHandlerActivityBehavior(delegateInstance)
        } else {
            super.getActivityBehaviorInstance(execution)
        }
    }
}
