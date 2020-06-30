package ru.tinkoff.top.camunda.delegator.info

const val CAMUNDA_DELEGATOR = "camundaDelegator"

/**
 * Delegator properties for actuator {@link InfoContributor}.
* */
data class DelegatorProperties(
    val enabled: Boolean
)
