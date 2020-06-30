package ru.tinkoff.top.camunda.delegator.springboot.configuration

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutorImpl
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.ResolveArgumentsInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.exception.DelegateExceptionProcessingInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.MultipleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateArgumentResolver

const val EXCEPTION_INTERCEPTOR_NUMBER = 1
const val ARGUMENTS_RESOLVER_INTERCEPTOR_NUMBER = 10
const val RESULT_PROCESSING_INTERCEPTOR_NUMBER = 500

@Configuration(proxyBeanMethods = false)
class CamundaDelegatesExecutorsConfiguration {

    @Bean("mainDelegateExecutor")
    @ConditionalOnMissingBean(name = ["mainDelegateExecutor"])
    fun mainDelegateExecutor(): DelegateExecutor = DelegateExecutorImpl()

    @Bean("delegateExceptionProcessingInterceptor")
    @Order(EXCEPTION_INTERCEPTOR_NUMBER)
    @ConditionalOnMissingBean(name = ["delegateExceptionProcessingInterceptor"])
    fun delegateExceptionProcessingInterceptor() = DelegateExceptionProcessingInterceptor()

    @Bean("resolveArgumentsInterceptor")
    @Order(ARGUMENTS_RESOLVER_INTERCEPTOR_NUMBER)
    @ConditionalOnMissingBean(name = ["resolveArgumentsInterceptor"])
    fun resolveArgumentsInterceptor(
        argumentResolvers: List<DelegateArgumentResolver>
    ) = ResolveArgumentsInterceptor(argumentResolvers)

    @Bean("singleResultInterceptor")
    @Order(RESULT_PROCESSING_INTERCEPTOR_NUMBER)
    @ConditionalOnMissingBean(name = ["singleResultInterceptor"])
    fun singleResultInterceptor() = SingleResultExecutionWriter()

    @Bean("multipleResultExecutionWriter")
    @Order(RESULT_PROCESSING_INTERCEPTOR_NUMBER)
    @ConditionalOnMissingBean(name = ["multipleResultExecutionWriter"])
    fun multipleResultExecutionWriter() = MultipleResultExecutionWriter()
}
