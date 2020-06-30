package ru.tinkoff.top.camunda.delegator.springboot.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.ContextVariableResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateExecutionResolver

@Configuration(proxyBeanMethods = false)
class CamundaDelegatorResolversConfiguration {

    @Bean
    fun contextVariableResolver() = ContextVariableResolver()

    @Bean
    fun delegateExecutionResolver() = DelegateExecutionResolver()
}
