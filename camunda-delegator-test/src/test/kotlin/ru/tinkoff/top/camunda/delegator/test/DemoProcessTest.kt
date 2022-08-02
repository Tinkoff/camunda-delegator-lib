package ru.tinkoff.top.camunda.delegator.test

import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageInMemProcessEngineConfiguration
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutorImpl
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.ResolveArgumentsInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.ContextVariableResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateExecutionResolver
import ru.tinkoff.top.camunda.delegator.parser.DefaultDelegatorBpmnParseFactory

class DemoProcessTest {

    companion object {
        private val delegateMethodHandlerRegister = DelegateMethodHandlerMockRegister(
            listOf(
                ResolveArgumentsInterceptor(listOf(ContextVariableResolver(), DelegateExecutionResolver())),
                SingleResultExecutionWriter()
            ),
            DelegateExecutorImpl()
        )

        private val camundaConfiguration = ProcessCoverageInMemProcessEngineConfiguration().also {
            it.bpmnParseFactory = DefaultDelegatorBpmnParseFactory()
        }

        @JvmField
        @RegisterExtension
        var coverageExtension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension
            .builder(camundaConfiguration)
            .assertClassCoverageAtLeast(0.5)
            .build()
    }

    @BeforeEach
    fun init() {
        delegateMethodHandlerRegister.initDelegates(listOf(DemoDelegate()))
    }

    @Test
    @Deployment(resources = ["processes/demo.bpmn"])
    fun demoProcess() {
        val processInstance = runProcess("demo")
        assertThat(processInstance).isStarted
        assertThat(processInstance).hasPassed("demoDelegateActivity")
        assertThat(processInstance).variables()
            .containsEntry("resultTest", TestResult("stroka", 5, false))
        assertThat(processInstance).isEnded
    }

    private fun runProcess(
        processName: String,
        variables: Map<String, Any?> = emptyMap()
    ): ProcessInstance = coverageExtension.processEngine.runtimeService.startProcessInstanceByKey(
        processName,
        processName,
        variables
    )
}
