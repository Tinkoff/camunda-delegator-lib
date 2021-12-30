package ru.tinkoff.top.camunda.delegator.config

import mu.KLogging
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin
import org.camunda.bpm.engine.impl.el.ExpressionManager

/**
 * Registers delegatorBpmnParseFactory as BpmnParseFactor and delegatorExpressionManager.
 */
class DelegatorBpmnParserCamundaPlugin(
    private val delegatorExpressionManager: ExpressionManager,
    private val delegatorBpmnParseFactory: BpmnParseFactory
) : ProcessEnginePlugin {

    companion object : KLogging()

    override fun preInit(processEngineConfiguration: ProcessEngineConfigurationImpl) {
        logger.info { "Bpm parser camunda plugin is started" }

        with(processEngineConfiguration) {
            expressionManager = delegatorExpressionManager
            bpmnParseFactory = delegatorBpmnParseFactory
        }
    }

    override fun postProcessEngineBuild(processEngine: ProcessEngine?) = Unit

    override fun postInit(processEngineConfiguration: ProcessEngineConfigurationImpl?) = Unit
}
