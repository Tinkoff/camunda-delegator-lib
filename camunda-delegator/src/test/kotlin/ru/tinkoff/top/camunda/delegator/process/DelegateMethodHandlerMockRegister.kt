package ru.tinkoff.top.camunda.delegator.process

import org.camunda.bpm.engine.test.mock.Mocks
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.register.DelegateMethodHandlerRegister
import ru.tinkoff.top.camunda.delegator.servicetask.MethodHandlerDelegate

open class DelegateMethodHandlerMockRegister(
    delegatesInterceptors: List<DelegateInterceptor>,
    mainDelegateExecutor: DelegateExecutor
) : DelegateMethodHandlerRegister(
    delegatesInterceptors,
    mainDelegateExecutor
) {
    override fun initDelegates(delegates: List<Any>) {
        createDelegateExecutors(delegates).forEach { (_, versions) ->
            versions.forEach { (_, executor) ->
                Mocks.register(executor.delegateInformation.metaInformation.delegateFullName, executor)
            }
        }
    }

    /**
     * Данный метод не должен использоваться в тестах, потому что делегаты достаются через
     * @see org.camunda.bpm.engine.test.mock.MockElResolver
     * который используется в
     * @see org.camunda.bpm.engine.test.mock.MockExpressionManager
     */
    override fun getDelegateMethodHandler(delegateName: String): MethodHandlerDelegate? {
        throw IllegalArgumentException("Use MockElResolver in test")
    }
}
