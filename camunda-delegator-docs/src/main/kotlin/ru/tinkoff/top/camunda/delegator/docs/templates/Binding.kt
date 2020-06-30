package ru.tinkoff.top.camunda.delegator.docs.templates

import java.io.Serializable

abstract class Binding : Serializable {
    abstract val type: String
}

data class PropertyBinding(
    val name: String
) : Binding() {
    override val type = "property"
}

data class InputParameterBinding(
    val name: String
) : Binding() {
    override val type = "camunda:inputParameter"
}

data class OutputParameterBinding(
    val source: String
) : Binding() {
    override val type = "camunda:outputParameter"
}
