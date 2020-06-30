package ru.tinkoff.top.camunda.delegator.docs.templates

interface CamundaTemplateLoader {
    fun load(): List<DelegateTemplate>
}
