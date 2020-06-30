package ru.tinkoff.top.camunda.delegator.springboot

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.tinkoff.top.camunda.delegator.docs.DelegateDocProperties
import ru.tinkoff.top.camunda.delegator.docs.DelegateDocService
import ru.tinkoff.top.camunda.delegator.docs.controller.DelegatorDocsEndpoint
import ru.tinkoff.top.camunda.delegator.docs.controller.DelegatorTemplatesEndpoint
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateInputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateOutputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.templates.CamundaTemplateLoader
import ru.tinkoff.top.camunda.delegator.docs.templates.DelegateTemplateCustomizer
import ru.tinkoff.top.camunda.delegator.docs.templates.DelegatorTemplateService
import ru.tinkoff.top.camunda.delegator.springboot.configuration.CamundaDelegatorDocsDescriptorConfiguration

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ru.tinkoff.top.camunda.delegator.docs.DelegateDocProperties::class)
@ConditionalOnProperty(
    value = ["delegator.docs.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
@Import(
    value = [
        CamundaDelegatorDocsDescriptorConfiguration::class
    ]
)
class DelegatorDocsAutoConfiguration {

    @Bean
    @ConditionalOnAvailableEndpoint(endpoint = DelegatorDocsEndpoint::class)
    fun delegatorDocsEndpoint(
        delegateDocService: DelegateDocService
    ): DelegatorDocsEndpoint {
        return DelegatorDocsEndpoint(delegateDocService)
    }

    @Bean
    @ConditionalOnAvailableEndpoint(endpoint = DelegatorTemplatesEndpoint::class)
    fun delegatorTemplatesEndpoint(
        templatesLoaders: List<CamundaTemplateLoader>
    ): DelegatorTemplatesEndpoint {
        return DelegatorTemplatesEndpoint(templatesLoaders)
    }

    @Bean
    fun delegatorTemplateService(
        customizers: List<DelegateTemplateCustomizer>
    ) = DelegatorTemplateService(customizers)

    @Bean
    fun delegateDocService(
        delegateDocProperties: ru.tinkoff.top.camunda.delegator.docs.DelegateDocProperties,
        inputDescriptors: List<DelegateInputDescriptor>,
        outputDescriptors: List<DelegateOutputDescriptor>
    ) = DelegateDocService(delegateDocProperties, inputDescriptors, outputDescriptors)
}
