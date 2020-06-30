package ru.tinkoff.top.camunda.delegator.springboot.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.activity.ActivityInstanceIdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.activity.CurrentActivityIdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.activity.CurrentActivityNameResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.activity.ParentActivityInstanceIdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.base.EventNameResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.base.IdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.bpmn.BpmnModelInstanceResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.bpmn.FlowElementResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.businesskey.BusinessKeyResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.definition.ProcessDefinitionIdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.execution.ParentIdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.execution.SuperExecutionResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.instacnce.ProcessInstanceIdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.instacnce.ProcessInstanceResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.status.IsCanceledResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.tenant.TenantIdResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.transition.CurrentTransitionIdResolver

@Configuration(proxyBeanMethods = false)
class CamundaDelegatorSubResolversConfiguration {

    @Bean
    fun activityInstanceIdResolver(): DelegateArgumentResolver = ActivityInstanceIdResolver()

    @Bean
    fun currentActivityIdResolver(): DelegateArgumentResolver = CurrentActivityIdResolver()

    @Bean
    fun currentActivityNameResolver(): DelegateArgumentResolver = CurrentActivityNameResolver()

    @Bean
    fun parentActivityInstanceIdResolver(): DelegateArgumentResolver = ParentActivityInstanceIdResolver()

    @Bean
    fun eventNameResolver(): DelegateArgumentResolver = EventNameResolver()

    @Bean
    fun idResolver(): DelegateArgumentResolver = IdResolver()

    @Bean
    fun bpmnModelInstanceResolver(): DelegateArgumentResolver = BpmnModelInstanceResolver()

    @Bean
    fun flowElementResolver(): DelegateArgumentResolver = FlowElementResolver()

    @Bean
    fun businessKeyResolver(): DelegateArgumentResolver = BusinessKeyResolver()

    @Bean
    fun processDefinitionIdResolver(): DelegateArgumentResolver = ProcessDefinitionIdResolver()

    @Bean
    fun parentIdResolver(): DelegateArgumentResolver = ParentIdResolver()

    @Bean
    fun superExecutionResolver(): DelegateArgumentResolver = SuperExecutionResolver()

    @Bean
    fun processInstanceIdResolver(): DelegateArgumentResolver = ProcessInstanceIdResolver()

    @Bean
    fun processInstanceResolver(): DelegateArgumentResolver = ProcessInstanceResolver()

    @Bean
    fun isCanceledResolver(): DelegateArgumentResolver = IsCanceledResolver()

    @Bean
    fun tenantIdResolver(): DelegateArgumentResolver = TenantIdResolver()

    @Bean
    fun currentTransitionIdResolver(): DelegateArgumentResolver = CurrentTransitionIdResolver()
}
