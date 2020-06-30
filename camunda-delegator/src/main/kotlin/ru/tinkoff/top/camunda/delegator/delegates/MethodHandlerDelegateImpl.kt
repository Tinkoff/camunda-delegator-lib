package ru.tinkoff.top.camunda.delegator.delegates

import mu.KLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.slf4j.MDC
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.servicetask.MethodHandlerDelegate

const val DELEGATE_NAME = "delegateName"
const val DELEGATE_VERSION = "delegateVersion"
const val DELEGATE_ACTIVITY_ID = "delegateActivityId"
const val DELEGATE_ACTIVITY_NAME = "delegateActivityName"

/**
 * Default implementation for {@link MethodHandlerDelegate}.
 *
 * Main steps:
 * 1. Init MDC context with activity and delegate information
 * 2. Call delegate executor
 * 3. Error processing: log exception and wrap error by DelegateException
 *
 * @see MethodHandlerDelegate
 * @see DelegateException

 * @author p.pletnev
 */
open class MethodHandlerDelegateImpl(
    override val delegateInformation: DelegateInformation,
    private val delegateExecutor: DelegateExecutor
) : MethodHandlerDelegate {

    companion object : KLogging()

    override fun execute(execution: DelegateExecution) {
        try {
            with(delegateInformation.metaInformation) {
                MDC.put(DELEGATE_NAME, delegateName)
                MDC.put(DELEGATE_VERSION, delegateVersion)
            }
            MDC.put(DELEGATE_ACTIVITY_ID, execution.currentActivityId)
            MDC.put(DELEGATE_ACTIVITY_NAME, execution.currentActivityName)

            delegateExecutor.execute(execution, delegateInformation, null)
        } catch (ex: Exception) {
            logger.error("Delegate execution is failed", ex)
            throw DelegateException(
                delegateName = delegateInformation.metaInformation.delegateFullName,
                delegateActivityId = execution.currentActivityId,
                delegateActivityName = execution.currentActivityName,
                cause = ex
            )
        } finally {
            MDC.remove(DELEGATE_NAME)
            MDC.remove(DELEGATE_VERSION)
            MDC.remove(DELEGATE_ACTIVITY_ID)
            MDC.remove(DELEGATE_ACTIVITY_NAME)
        }
    }
}
