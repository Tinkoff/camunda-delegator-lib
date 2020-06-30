package ru.tinkoff.top.camunda.delegator.docs.model

import io.swagger.v3.oas.models.media.Schema

class DelegatesDoc(
    val delegatesInfo: List<DelegateInfo>,
    val components: DelegateComponents
)

class DelegateHelperInfo(
    val helperName: String,
    val title: String,
    val description: String?,
    val helperMethodsInfo: List<HelperMethodInfo>
)

class HelperMethodInfo(
    val name: String,
    val title: String,
    val description: String?,
    val inputVariables: List<InputVariableInfo>,
    val outputVariables: List<OutputVariableInfo>
)

class DelegateComponents(
    val schemas: Map<String, Schema<*>>
)

class DelegateInfo(
    val delegateName: String,
    val delegateVersion: String,
    val delegateFullName: String,
    val delegateAliases: Set<String>,
    val title: String,
    val description: String?,
    val isAffectsProcessExecution: Boolean,
    val rawReturnValue: Schema<*>? = null,
    val inputVariables: List<InputVariableInfo>,
    val outputVariables: List<OutputVariableInfo>
)

open class VariableInfo(
    val name: String,
    val isRequired: Boolean? = null,
    val isLocal: Boolean? = null,
    val description: String?,
    val simpleClassName: String,
    val schema: Schema<*>?
)

class InputVariableInfo(
    name: String,
    isRequired: Boolean? = null,
    isLocal: Boolean? = null,
    schema: Schema<*>?,
    simpleClassName: String,
    description: String? = null,
    val isSystem: Boolean
) : VariableInfo(name, isRequired, isLocal, description, simpleClassName, schema)

class OutputVariableInfo(
    name: String,
    isRequired: Boolean? = null,
    isLocal: Boolean? = null,
    description: String? = null,
    simpleClassName: String,
    schema: Schema<*>?
) : VariableInfo(name, isRequired, isLocal, description, simpleClassName, schema)
