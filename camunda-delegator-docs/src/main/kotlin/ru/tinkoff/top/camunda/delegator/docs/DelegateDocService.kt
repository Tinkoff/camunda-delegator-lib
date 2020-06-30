package ru.tinkoff.top.camunda.delegator.docs

import io.swagger.v3.oas.models.Components
import mu.KLogging
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.exception.ExceptionProcessingStrategy
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.exception.ExceptionStrategy
import ru.tinkoff.top.camunda.delegator.delegates.getClassAnnotation
import ru.tinkoff.top.camunda.delegator.delegates.getMethodAnnotation
import ru.tinkoff.top.camunda.delegator.delegates.register.DelegateMethodHandlerRegister
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateInputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.DelegateOutputDescriptor
import ru.tinkoff.top.camunda.delegator.docs.descriptors.getSchema
import ru.tinkoff.top.camunda.delegator.docs.model.DelegateComponents
import ru.tinkoff.top.camunda.delegator.docs.model.DelegateInfo
import ru.tinkoff.top.camunda.delegator.docs.model.DelegatesDoc
import java.io.InputStreamReader
import java.lang.reflect.Method
import ru.tinkoff.top.camunda.delegator.docs.annotations.DelegateInfo as DelegateInfoAnn

class DelegateDocService(
    private val delegateDocProperties: ru.tinkoff.top.camunda.delegator.docs.DelegateDocProperties,
    private val inputDescriptors: List<DelegateInputDescriptor>,
    private val delegateResultsDescriptor: List<DelegateOutputDescriptor>
) : ApplicationListener<ApplicationStartedEvent> {

    companion object : KLogging()

    private lateinit var delegatesDoc: DelegatesDoc

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        try {
            val applicationContext = event.applicationContext
            val delegateBeans = applicationContext.getBeansWithAnnotation(CamundaDelegate::class.java)

            val components = Components()
            val delegatesInfo = delegateBeans.flatMap { (_, bean) ->
                try {
                    getDelegateInfo(components, bean.javaClass)
                } catch (ex: Exception) {
                    logger.error(ex) { "Failed creating docs for delegate ${bean.javaClass}" }
                    emptyList()
                }
            }

            delegatesDoc = DelegatesDoc(delegatesInfo, DelegateComponents(components.schemas))
        } catch (ex: Exception) {
            logger.error(ex) { "Failed creating delegate docs" }
        }
    }

    fun getDelegatesDoc(): DelegatesDoc {
        return delegatesDoc
    }

    private fun getDelegateInfo(
        components: Components,
        delegateClass: Class<*>
    ): List<DelegateInfo> {
        val methods = MethodIntrospector.selectMethods(
            delegateClass,
            DelegateMethodHandlerRegister.DELEGATE_EXECUTION_METHODS
        )
        return methods.map { executionMethod ->
            val metaInformation = DelegateMetaInformation(delegateClass, executionMethod)

            val inputVariables = metaInformation.methodParameters.flatMap {
                inputDescriptors.mapNotNull { descriptor -> descriptor.describeInputVariable(components, it) }
            }
            val outputVariables =
                delegateResultsDescriptor.flatMap { it.describeResultValues(components, metaInformation) }

            DelegateInfo(
                delegateName = metaInformation.delegateName,
                delegateVersion = metaInformation.delegateVersion,
                delegateFullName = metaInformation.delegateFullName,
                delegateAliases = metaInformation.delegateAliases,
                title = getDelegateTitle(metaInformation) ?: "",
                description = getDescription(metaInformation),
                isAffectsProcessExecution = isMethodAffectsProcessExecution(executionMethod),
                rawReturnValue = getSchema(components, metaInformation.returnParameter),
                inputVariables = inputVariables,
                outputVariables = outputVariables
            )
        }
    }

    private fun getDescription(metaInformation: DelegateMetaInformation): String? {
        val fileName = delegateDocProperties.descriptionsFolder + metaInformation.delegateFullName + ".html"
        try {
            val resource = ClassPathResource(fileName, Thread.currentThread().contextClassLoader)
            if (!resource.exists()) {
                return null
            }
            resource.inputStream.use {
                return FileCopyUtils.copyToString(InputStreamReader(it, Charsets.UTF_8))
            }
        } catch (ex: Exception) {
            logger.error(ex) { "Failed loading info by name = $fileName" }
            return null
        }
    }

    private fun isMethodAffectsProcessExecution(method: Method): Boolean {
        val exceptionStrategy = AnnotatedElementUtils.findMergedAnnotation(method, ExceptionStrategy::class.java)
            ?: return true
        return when (exceptionStrategy.exceptionProcessingStrategy) {
            ExceptionProcessingStrategy.FORWARD -> true
            ExceptionProcessingStrategy.LOG -> false
        }
    }
}

fun getDelegateTitle(delegateMetaInformation: DelegateMetaInformation): String? {
    val delegateAnn = delegateMetaInformation.getMethodAnnotation()
        ?: delegateMetaInformation.getClassAnnotation<DelegateInfoAnn>()
    return delegateAnn?.title
}
