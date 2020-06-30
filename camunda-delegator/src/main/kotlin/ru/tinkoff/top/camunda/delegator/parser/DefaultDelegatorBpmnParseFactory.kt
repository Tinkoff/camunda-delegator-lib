package ru.tinkoff.top.camunda.delegator.parser

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser
import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory

class DefaultDelegatorBpmnParseFactory : BpmnParseFactory {
    override fun createBpmnParse(bpmnParser: BpmnParser): BpmnParse {
        return DelegatorCamundaBpmnParse(bpmnParser)
    }
}
