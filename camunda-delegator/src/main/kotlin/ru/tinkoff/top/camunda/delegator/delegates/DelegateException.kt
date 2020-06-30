package ru.tinkoff.top.camunda.delegator.delegates

/**
 * Exception with delegate and activity information
* */
class DelegateException(
    val delegateName: String,
    val delegateActivityId: String,
    val delegateActivityName: String?,
    cause: Throwable?
) : RuntimeException(cause)
