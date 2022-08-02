package ru.tinkoff.top.camunda.delegator.parser

import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl
import org.camunda.bpm.engine.impl.util.xml.Element
import ru.tinkoff.top.camunda.delegator.servicetask.CustomDelegateExpressionExecutionListener
import ru.tinkoff.top.camunda.delegator.servicetask.CustomServiceTaskDelegateExpressionActivityBehavior

/**
 * Extension of {@link BpmnParse} for custom delegate handling
 *
 * @see CustomServiceTaskDelegateExpressionActivityBehavior
 *
 * @author p.pletnev
 */
open class DelegatorCamundaBpmnParse(
    parser: BpmnParser
) : BpmnParse(parser) {

    override fun parseServiceTaskLike(
        activity: ActivityImpl,
        elementName: String,
        serviceTaskElement: Element,
        camundaPropertiesElement: Element,
        scope: ScopeImpl
    ) {
        val delegateExpression = serviceTaskElement.attributeNS(
            CAMUNDA_BPMN_EXTENSIONS_NS,
            PROPERTYNAME_DELEGATE_EXPRESSION
        )

        if (delegateExpression != null) {
            val resultVariableName = parseResultVariable(serviceTaskElement)

            if (resultVariableName != null) {
                addError(
                    "'resultVariableName' not supported for $elementName elements using 'delegateExpression'",
                    serviceTaskElement
                )
            }
            activity.activityBehavior = CustomServiceTaskDelegateExpressionActivityBehavior(
                expressionManager.createExpression(delegateExpression),
                parseFieldDeclarations(serviceTaskElement)
            )
        } else {
            super.parseServiceTaskLike(
                activity,
                elementName,
                serviceTaskElement,
                camundaPropertiesElement,
                scope
            )
        }
    }

    override fun parseExecutionListener(
        executionListenerElement: Element,
        ancestorElementId: String
    ): ExecutionListener? {
        var executionListener: ExecutionListener? = null

        val delegateExpression = executionListenerElement.attribute(
            PROPERTYNAME_DELEGATE_EXPRESSION
        )

        if (delegateExpression != null) {
            if (delegateExpression.isEmpty()) {
                addError(
                    "Attribute 'delegateExpression' cannot be empty",
                    executionListenerElement,
                    ancestorElementId
                )
            } else {
                executionListener = CustomDelegateExpressionExecutionListener(
                    expressionManager.createExpression(delegateExpression),
                    parseFieldDeclarations(executionListenerElement)
                )
            }
        } else {
            return super.parseExecutionListener(executionListenerElement, ancestorElementId)
        }

        return executionListener
    }
}
