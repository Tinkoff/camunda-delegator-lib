package ru.tinkoff.top.camunda.delegator.docs.templates

import mu.KLogging
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.MultipleResults
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.annotaions.ResultVariable
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultVariable
import ru.tinkoff.top.camunda.delegator.delegates.register.DelegateMethodHandlerRegister
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getParameterName
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.getVariableAnnotation
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getVariableDescription
import ru.tinkoff.top.camunda.delegator.docs.getDelegateTitle
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.getParameterName as getResultParameterName

const val GENERATED_PREFIX = "Generated"
const val SERVICE_TASK_NAME = "bpmn:ServiceTask"
const val DELEGATE_EXPRESSION_PROPERTY_NAME = "camunda:delegateExpression"

/**
 * Описание структуры шаблона делегата:
 * https://github.com/camunda/camunda-modeler/tree/master/docs/element-templates.
 * */
class DelegatorTemplateService(
    private val customizers: List<DelegateTemplateCustomizer> = emptyList()
) : ApplicationListener<ApplicationStartedEvent>, CamundaTemplateLoader {

    companion object : KLogging()

    private lateinit var delegatesTemplates: List<DelegateTemplate>

    override fun load(): List<DelegateTemplate> = delegatesTemplates

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val applicationContext = event.applicationContext
        val delegateBeans = applicationContext.getBeansWithAnnotation(CamundaDelegate::class.java)
        initTemplates(delegateBeans.values.toList())
    }

    fun initTemplates(delegateBeans: List<Any>) {
        try {
            val delegatesTemplates = delegateBeans.flatMap { bean ->
                try {
                    val delegateClass = bean.javaClass
                    val methods = MethodIntrospector.selectMethods(
                        delegateClass,
                        DelegateMethodHandlerRegister.DELEGATE_EXECUTION_METHODS
                    )
                    methods.map { executionMethod ->
                        val delegateInfo = DelegateMetaInformation(
                            delegateClass = delegateClass,
                            executionDelegateMethod = executionMethod
                        )
                        customizeTemplate(createTemplate(delegateInfo), delegateInfo)
                    }
                } catch (ex: Exception) {
                    logger.error(ex) { "Failed creating template for delegate ${bean.javaClass}" }
                    emptyList()
                }
            }
            this.delegatesTemplates = delegatesTemplates.sortedBy { it.id }
        } catch (ex: Exception) {
            logger.error(ex) { "Failed creating template for delegates" }
        }
    }

    private fun createTemplate(
        delegateMetaInformation: DelegateMetaInformation
    ): DelegateTemplate {
        return DelegateTemplate(
            id = "${GENERATED_PREFIX}_${delegateMetaInformation.delegateFullName}",
            name = getDelegateTitle(delegateMetaInformation)
                ?: delegateMetaInformation.delegateFullName,
            appliesTo = listOf(SERVICE_TASK_NAME),
            properties = listOf(createPropertyForDelegate(delegateMetaInformation)) +
                createInputProperties(delegateMetaInformation) +
                createOutputProperties(delegateMetaInformation)
        )
    }

    private fun customizeTemplate(
        delegateTemplate: DelegateTemplate,
        metaInformation: DelegateMetaInformation
    ): DelegateTemplate {
        var newTemplate = delegateTemplate
        this.customizers.forEach { newTemplate = it.customize(newTemplate, metaInformation) }
        return newTemplate
    }

    private fun createPropertyForDelegate(
        delegateMetaInformation: DelegateMetaInformation
    ): Property {
        return Property(
            type = PropertyType.STRING.type,
            label = "Delegate expression",
            value = "\${${delegateMetaInformation.delegateFullName}}",
            editable = false,
            binding = PropertyBinding(
                name = DELEGATE_EXPRESSION_PROPERTY_NAME
            )
        )
    }

    private fun createInputProperties(
        delegateMetaInformation: DelegateMetaInformation
    ): List<Property> {
        return delegateMetaInformation.methodParameters.mapNotNull { methodParameter ->
            val ann = getVariableAnnotation(methodParameter)
                ?: return@mapNotNull null
            if (!ann.isLocal) {
                return@mapNotNull null
            }
            val parameterName = getParameterName(methodParameter, ann)
            return@mapNotNull Property(
                type = PropertyType.STRING.type,
                label = parameterName,
                description = getVariableDescription(methodParameter.parameter),
                binding = InputParameterBinding(parameterName),
                constraints = Constraints(
                    notEmpty = !methodParameter.isOptional
                )
            )
        }
    }

    private fun createOutputProperties(
        delegateMetaInformation: DelegateMetaInformation
    ): List<Property> {
        val executionMethod = delegateMetaInformation.executionDelegateMethod
        if (executionMethod.returnType == Void.TYPE) {
            return emptyList()
        }
        return getOutputVariableForSingleResult(delegateMetaInformation) +
            getOutputVariablesForMultipleResults(delegateMetaInformation)
    }

    private fun getOutputVariableForSingleResult(
        delegateMetaInformation: DelegateMetaInformation
    ): List<Property> {
        val executionMethod = delegateMetaInformation.executionDelegateMethod
        val resultVariable =
            AnnotatedElementUtils.findMergedAnnotation(
                executionMethod,
                SingleResultVariable::class.java
            ) ?: return emptyList()

        if (!resultVariable.isLocal) {
            return emptyList()
        }
        return listOf(
            Property(
                type = PropertyType.STRING.type,
                label = resultVariable.name,
                description = getVariableDescription(executionMethod),
                binding = OutputParameterBinding(
                    source = "\${${resultVariable.name}}"
                )
            )
        )
    }

    private fun getOutputVariablesForMultipleResults(
        delegateMetaInformation: DelegateMetaInformation
    ): List<Property> {
        val executionMethod = delegateMetaInformation.executionDelegateMethod

        AnnotatedElementUtils.findMergedAnnotation(
            executionMethod,
            MultipleResults::class.java
        ) ?: return emptyList()

        val resultClass = executionMethod.returnType.kotlin

        return resultClass.memberProperties.mapNotNull { p ->
            val info = p.findAnnotation<ResultVariable>() ?: return@mapNotNull null

            if (!info.isLocal) {
                return@mapNotNull null
            }
            val paramName = getResultParameterName(p, info)

            Property(
                type = PropertyType.STRING.type,
                label = paramName,
                description = getVariableDescription(p),
                binding = OutputParameterBinding(
                    source = "\${$paramName}"
                )
            )
        }
    }
}
