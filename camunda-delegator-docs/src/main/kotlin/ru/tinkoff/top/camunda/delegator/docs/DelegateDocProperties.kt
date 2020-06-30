package ru.tinkoff.top.camunda.delegator.docs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("delegator.docs")
data class DelegateDocProperties(
    val descriptionsFolder: String = "delegates/"
)
