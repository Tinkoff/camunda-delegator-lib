package ru.tinkoff.top.camunda.delegator.docs.descriptors

import io.swagger.v3.oas.models.Components
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.docs.model.OutputVariableInfo

interface DelegateOutputDescriptor {

    fun describeResultValues(
        components: Components,
        metaInformation: DelegateMetaInformation
    ): List<OutputVariableInfo>
}
