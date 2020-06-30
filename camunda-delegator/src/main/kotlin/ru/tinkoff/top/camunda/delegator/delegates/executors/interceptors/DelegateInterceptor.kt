package ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors

import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor

/**
 * @author p.pletnev
 * */
abstract class DelegateInterceptor : DelegateExecutor {

    lateinit var nextExecutor: DelegateExecutor
}
