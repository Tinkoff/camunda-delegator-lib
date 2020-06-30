package ru.tinkoff.top.camunda.delegator.docs.templates

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class DelegateTemplate(
    @JsonProperty("\$schema")
    var schema: String? = null,
    var id: String,
    var name: String,
    var version: Int? = null,
    @JsonProperty("isDefault")
    var isDefault: Boolean? = null,
    var appliesTo: List<String>,
    var properties: List<Property>,
    var entriesVisible: EntriesVisible? = EntriesVisible()
) : Serializable

class Property(
    var type: String? = null,
    var label: String? = null,
    var description: String? = null,
    var value: String? = null,
    var choices: List<Choice>? = null,
    var editable: Boolean? = null,
    var binding: Binding,
    var constraints: Constraints? = null
) : Serializable

class Choice(
    var name: String,
    var value: String
)

@Deprecated("Select which entries are visible in the properties panel")
class EntriesVisible(
    @JsonProperty("_all")
    var all: Boolean = true
) : Serializable

class Constraints(
    var notEmpty: Boolean?,
    var minLength: String? = null,
    var maxLength: String? = null,
    var pattern: ConstraintPattern? = null
) : Serializable

class ConstraintPattern(
    var value: String?,
    var message: String?
)

enum class PropertyType(val type: String) {
    STRING("String"), DROPDOWN("Dropdown")
}
