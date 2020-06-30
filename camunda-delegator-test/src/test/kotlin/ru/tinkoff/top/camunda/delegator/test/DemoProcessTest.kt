package ru.tinkoff.top.camunda.delegator.test

import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
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

        private val camundaConfiguration = TestCamundaConfiguration().also {
            it.setBpmnParseFactory(DefaultDelegatorBpmnParseFactory())
        }

        @Rule
        @ClassRule
        @JvmField
        val processEngineRule: TestCoverageProcessEngineRule = TestCoverageProcessEngineRuleBuilder
            .create(camundaConfiguration.buildProcessEngine())
            .build()
    }

    @Before
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
    ): ProcessInstance = runtimeService().startProcessInstanceByKey(
        processName,
        processName,
        variables
    )
}
