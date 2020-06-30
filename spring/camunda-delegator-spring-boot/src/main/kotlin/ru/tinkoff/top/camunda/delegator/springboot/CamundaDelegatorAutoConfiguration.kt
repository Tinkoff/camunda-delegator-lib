package ru.tinkoff.top.camunda.delegator.springboot

import org.camunda.bpm.engine.impl.cfg.BpmnParseFactory
import org.camunda.bpm.engine.impl.cfg.CompositeProcessEnginePlugin
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin
import org.camunda.bpm.engine.impl.el.ExpressionManager
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration
import org.camunda.bpm.spring.boot.starter.util.CamundaSpringBootUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration
import org.springframework.boot.actuate.info.SimpleInfoContributor
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import ru.tinkoff.top.camunda.delegator.config.BpmnParserCamundaPlugin
import ru.tinkoff.top.camunda.delegator.config.SpringProcessEngineBpmnParseConfiguration
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.factory.MethodHandlerDelegateFactory
import ru.tinkoff.top.camunda.delegator.delegates.factory.MethodHandlerDelegateFactoryImpl
import ru.tinkoff.top.camunda.delegator.delegates.register.DelegateExecutorsRegister
import ru.tinkoff.top.camunda.delegator.info.CAMUNDA_DELEGATOR
import ru.tinkoff.top.camunda.delegator.info.DelegatorProperties
import ru.tinkoff.top.camunda.delegator.parser.DefaultDelegatorBpmnParseFactory
import ru.tinkoff.top.camunda.delegator.servicetask.DefaultDelegatorExpressionManager
import ru.tinkoff.top.camunda.delegator.springboot.configuration.CamundaDelegatesExecutorsConfiguration
import ru.tinkoff.top.camunda.delegator.springboot.configuration.CamundaDelegatorResolversConfiguration
import ru.tinkoff.top.camunda.delegator.springboot.configuration.CamundaDelegatorSubResolversConfiguration
import ru.tinkoff.top.camunda.delegator.springboot.listener.DelegateMethodHandlerEventRegister

const val DELEGATOR_EXPRESSION_MANAGER = "delegatorExpressionManager"
const val DELEGATOR_BPMN_PARSE_FACTORY = "delegatorBpmnParseFactory"

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    value = ["delegator.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
@Import(
    value = [
        CamundaDelegatesExecutorsConfiguration::class,
        CamundaDelegatorResolversConfiguration::class,
        CamundaDelegatorSubResolversConfiguration::class
    ]
)
@AutoConfigureBefore(CamundaBpmAutoConfiguration::class)
class CamundaDelegatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ProcessEngineConfigurationImpl::class)
    fun processEngineConfigurationImpl(
        processEnginePlugins: List<ProcessEnginePlugin>
    ): ProcessEngineConfigurationImpl {
        val configuration =
            CamundaSpringBootUtil.initCustomFields(SpringProcessEngineBpmnParseConfiguration())
        configuration.processEnginePlugins.add(CompositeProcessEnginePlugin(processEnginePlugins))
        return configuration
    }

    @Bean
    fun bpmnParserCamundaPlugin(
        @Qualifier(DELEGATOR_EXPRESSION_MANAGER)
        delegatorExpressionManager: ExpressionManager,
        @Qualifier(DELEGATOR_BPMN_PARSE_FACTORY)
        delegatorBpmnParseFactory: BpmnParseFactory
    ): BpmnParserCamundaPlugin {
        return BpmnParserCamundaPlugin(delegatorExpressionManager, delegatorBpmnParseFactory)
    }

    @Bean
    @ConditionalOnMissingBean(MethodHandlerDelegateFactory::class)
    fun methodHandlerDelegateFactory(): MethodHandlerDelegateFactory {
        return MethodHandlerDelegateFactoryImpl()
    }

    @Bean("delegatorDelegateRegister")
    @ConditionalOnMissingBean(name = ["delegatorDelegateRegister"])
    fun delegatorDelegateRegister(
        delegateInterceptor: List<DelegateInterceptor>,
        @Qualifier("mainDelegateExecutor")
        mainDelegateExecutor: DelegateExecutor,
        methodHandlerDelegateFactory: MethodHandlerDelegateFactory
    ): DelegateExecutorsRegister = DelegateMethodHandlerEventRegister(
        delegateInterceptor,
        mainDelegateExecutor,
        methodHandlerDelegateFactory
    )

    @Bean(DELEGATOR_EXPRESSION_MANAGER)
    @ConditionalOnMissingBean(name = [DELEGATOR_EXPRESSION_MANAGER])
    fun delegatorExpressionManager(
        applicationContext: ApplicationContext,
        delegateMethodHandlerRegister: DelegateExecutorsRegister
    ): ExpressionManager =
        DefaultDelegatorExpressionManager(applicationContext, delegateMethodHandlerRegister)

    @Bean(DELEGATOR_BPMN_PARSE_FACTORY)
    @ConditionalOnMissingBean(name = [DELEGATOR_BPMN_PARSE_FACTORY])
    fun delegatorBpmnParseFactory(): BpmnParseFactory = DefaultDelegatorBpmnParseFactory()

    @Bean
    @ConditionalOnEnabledInfoContributor(CAMUNDA_DELEGATOR)
    @Order(InfoContributorAutoConfiguration.DEFAULT_ORDER)
    fun delegatorInfoContributor() =
        SimpleInfoContributor(CAMUNDA_DELEGATOR, DelegatorProperties(true))
}
