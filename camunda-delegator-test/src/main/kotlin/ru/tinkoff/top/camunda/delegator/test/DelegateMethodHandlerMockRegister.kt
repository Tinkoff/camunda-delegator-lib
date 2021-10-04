package ru.tinkoff.top.camunda.delegator.test

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
                val meta = executor.delegateInformation.metaInformation
                Mocks.register(meta.delegateFullName, executor)

                meta.delegateAliases.forEach {
                    Mocks.register(it, executor)
                }
            }
        }
    }

    /**
     * This method don`t use in test, because for delegate resolvers use
     * @see org.camunda.bpm.engine.test.mock.MockElResolver
     * which are used in
     * @see org.camunda.bpm.engine.test.mock.MockExpressionManager
     */
    override fun getDelegateMethodHandler(delegateName: String): MethodHandlerDelegate? {
        throw IllegalArgumentException("Use MockElResolver in test")
    }
}
