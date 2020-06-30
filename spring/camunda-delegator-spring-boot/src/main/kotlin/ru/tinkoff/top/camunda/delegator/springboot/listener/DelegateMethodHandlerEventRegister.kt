package ru.tinkoff.top.camunda.delegator.springboot.listener

import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.annotation.Order
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.factory.MethodHandlerDelegateFactory
import ru.tinkoff.top.camunda.delegator.delegates.register.DelegateMethodHandlerRegister

const val DELEGATE_REGISTER_LISTENER_ORDER = 1000

@Order(DELEGATE_REGISTER_LISTENER_ORDER)
open class DelegateMethodHandlerEventRegister(
    delegatesInterceptors: List<DelegateInterceptor>,
    mainDelegateExecutor: DelegateExecutor,
    methodHandlerDelegateFactory: MethodHandlerDelegateFactory
) : DelegateMethodHandlerRegister(
    delegatesInterceptors,
    mainDelegateExecutor,
    methodHandlerDelegateFactory
),
    ApplicationListener<ApplicationStartedEvent> {

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val delegates =
            event.applicationContext.getBeansWithAnnotation(CamundaDelegate::class.java).values.toList()
        initDelegates(delegates)
    }
}
