# camunda-delegator-lib

This project provides declarative camunda delegates for Spring based application

## Requirements

1. Project complied with Camunda version 7.13.
2. Spring Boot 2.5.2
3. Kotlin version 1.5

## Installation

Add dependency in gradle
```kotlin
implementation(group = "ru.tinkoff.top", name = "camunda-delegator-spring-boot-starter", version = "version")
```

Activate library by camunda plugin `BpmnParserCamundaPlugin` with other plugins

```kotlin
    @Bean
    fun processEngineConfiguration(
        processEnginePlugins: List<ProcessEnginePlugin>
    ): SpringProcessEngineConfiguration {
        val customEngine = SpringProcessEngineBpmnParseConfiguration()
        return CamundaSpringBootUtil.initCustomFields(customEngine).also {
            it.isJobExecutorActivate = false
            it.processEnginePlugins.add(CompositeProcessEnginePlugin(processEnginePlugins))
        }
    }
```

or

```kotlin
    @Bean
    fun processEngineConfiguration(
        @Qualifier(DELEGATOR_EXPRESSION_MANAGER)
        delegatorExpressionManager: ExpressionManager,
        @Qualifier(DELEGATOR_BPMN_PARSE_FACTORY)
        delegatorBpmnParseFactory: BpmnParseFactory
    ): SpringProcessEngineConfiguration {
        val customEngine = SpringProcessEngineBpmnParseConfiguration()
        return CamundaSpringBootUtil.initCustomFields(customEngine).also {
            it.isJobExecutorActivate = false
            it.processEnginePlugins.add(
                BpmnParserCamundaPlugin(delegatorExpressionManager, delegatorBpmnParseFactory)
            )
        }
    }
```

## Notice

Camunda `JobExecutor` cannot be activated before application started it fully. 
Your can activate job executing on event `ApplicationStartedEvent`(delegators init on this event).

```kotlin
@Component
class JobExecutorActivationListener(
    private val jobExecutor: JobExecutor
) {

    @EventListener(ApplicationStartedEvent::class)
    @Order(DELEGATE_REGISTER_LISTENER_ORDER + 1)
    fun startJobExecution() {
        jobExecutor.start()
    }
}
```

## Example

```kotlin
@CamundaDelegate
class ProductGeneratorDelegate {

    @DelegateExecute
    @SingleResultVariable(name = "product")
    fun generateProduct(
        @Variable(name = "product") product: String
    ): Product {
        return Product(product)
    }
}
```

## Contacts
p.pletnev@tinkoff.ru 
