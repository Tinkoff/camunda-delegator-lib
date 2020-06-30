package ru.tinkoff.top.camunda.delegator.delegates.register

import mu.KLogging
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.util.ReflectionUtils
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.factory.MethodHandlerDelegateFactory
import ru.tinkoff.top.camunda.delegator.delegates.factory.MethodHandlerDelegateFactoryImpl
import ru.tinkoff.top.camunda.delegator.servicetask.MethodHandlerDelegate
import java.lang.reflect.Method

open class DelegateMethodHandlerRegister(
    delegatesInterceptors: List<DelegateInterceptor>,
    mainDelegateExecutor: DelegateExecutor,
    private val methodHandlerDelegateFactory: MethodHandlerDelegateFactory = MethodHandlerDelegateFactoryImpl()
) : DelegateExecutorsRegister {

    companion object : KLogging() {
        val DELEGATE_EXECUTION_METHODS = ReflectionUtils.MethodFilter { method: Method ->
            AnnotatedElementUtils.hasAnnotation(method, DelegateExecute::class.java)
        }
    }

    private var delegateExecutorsChains: DelegateExecutor

    init {
        if (delegatesInterceptors.isEmpty()) {
            delegateExecutorsChains = mainDelegateExecutor
        } else {
            // Expanding the call chain
            for (i in 0 until delegatesInterceptors.size - 1) {
                delegatesInterceptors[i].nextExecutor = delegatesInterceptors[i + 1]
            }
            delegatesInterceptors.last().nextExecutor = mainDelegateExecutor

            delegateExecutorsChains = delegatesInterceptors.first()
        }
    }

    private lateinit var executorsByNameWithVersion: Map<String, MethodHandlerDelegate>

    override fun getDelegateMethodHandler(delegateName: String): MethodHandlerDelegate? {
        return executorsByNameWithVersion[delegateName]
    }

    open fun initDelegates(
        delegates: List<Any>
    ) {
        val delegatesByFullName = HashMap<String, MethodHandlerDelegate>()

        createDelegateExecutors(delegates).forEach { (_, versions) ->
            versions.forEach { (_, executor) ->
                delegatesByFullName[executor.delegateInformation.metaInformation.delegateFullName] =
                    executor
                initDelegateAliases(executor, delegatesByFullName)
            }
        }

        executorsByNameWithVersion = delegatesByFullName
    }

    protected fun createDelegateExecutors(
        delegates: List<Any>
    ): Map<String, Map<String, MethodHandlerDelegate>> {
        val executorsByDelegateNameAndVersion =
            HashMap<String, HashMap<String, MethodHandlerDelegate>>()
        delegates.forEach { delegateBean ->
            AnnotatedElementUtils.findMergedAnnotation(
                delegateBean.javaClass,
                CamundaDelegate::class.java
            )
                ?: throw IllegalArgumentException("Class ${delegateBean.javaClass} has not annotation @CamundaDelegate")

            val methods =
                MethodIntrospector.selectMethods(delegateBean.javaClass, DELEGATE_EXECUTION_METHODS)
            methods.forEach { delegateMethod ->
                val metaInformation =
                    DelegateMetaInformation(delegateBean::class.java, delegateMethod)

                val delegateName = metaInformation.delegateName
                val delegateVersions =
                    executorsByDelegateNameAndVersion.computeIfAbsent(delegateName) { HashMap() }

                val delegateInformation = DelegateInformation(
                    delegateBean = delegateBean,
                    metaInformation = metaInformation
                )

                val methodHandlerDelegate = createMethodHandlerDelegate(delegateInformation)
                val definedVersion = delegateVersions[metaInformation.delegateVersion]

                if (definedVersion == null) {
                    delegateVersions[metaInformation.delegateVersion] =
                        methodHandlerDelegate
                } else {
                    val firstDelegateDesc =
                        definedVersion.delegateInformation.metaInformation.getShortDescription()
                    val secondDelegateDesc = metaInformation.getShortDescription()
                    throw IllegalArgumentException(
                        "Delegate $delegateName with version ${metaInformation.delegateVersion} defined twice. " +
                            "First in class $firstDelegateDesc. Second in class $secondDelegateDesc."
                    )
                }
            }
        }
        return executorsByDelegateNameAndVersion
    }

    private fun createMethodHandlerDelegate(
        delegateInformation: DelegateInformation
    ): MethodHandlerDelegate {
        return methodHandlerDelegateFactory.createMethodHandlerDelegate(
            delegateInformation,
            delegateExecutorsChains
        )
    }

    private fun initDelegateAliases(
        delegateHandler: MethodHandlerDelegate,
        delegatesWithVersion: HashMap<String, MethodHandlerDelegate>
    ) {
        val aliases = delegateHandler.delegateInformation.metaInformation.delegateAliases
        aliases.forEach { alias ->
            val presentDelegate = delegatesWithVersion[alias]
            if (presentDelegate != null) {
                val firstDesc =
                    presentDelegate.delegateInformation.metaInformation.getShortDescription()
                val secondDesc =
                    delegateHandler.delegateInformation.metaInformation.getShortDescription()
                throw IllegalArgumentException("Alias $alias used twice. First $firstDesc. Second $secondDesc")
            }
            delegatesWithVersion[alias] = delegateHandler
        }
    }
}
