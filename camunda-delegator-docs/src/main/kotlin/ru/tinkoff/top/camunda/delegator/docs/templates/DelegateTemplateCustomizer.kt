package ru.tinkoff.top.camunda.delegator.docs.templates

import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation

@FunctionalInterface
interface DelegateTemplateCustomizer {
    fun customize(
        template: DelegateTemplate,
        metaInformation: DelegateMetaInformation
    ): DelegateTemplate
}
