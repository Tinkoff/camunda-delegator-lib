package ru.tinkoff.top.camunda.delegator.springboot.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateInputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateOutputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.input.CamundaContextVariableDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.input.DelegateExecutionDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.output.MultipleResultOutputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.output.SingleResultOutputDescriptor

@Configuration(proxyBeanMethods = false)
class CamundaDelegatorDocsDescriptorConfiguration {

    @Bean
    fun delegateExecutionDescriptor(): DelegateInputDescriptor = DelegateExecutionDescriptor()

    @Bean
    fun camundaContextVariableDescriptor(): DelegateInputDescriptor = CamundaContextVariableDescriptor()

    @Bean
    fun multipleResultOutputDescriptor(): DelegateOutputDescriptor = MultipleResultOutputDescriptor()

    @Bean
    fun singleResultOutputDescriptor(): DelegateOutputDescriptor = SingleResultOutputDescriptor()
}
