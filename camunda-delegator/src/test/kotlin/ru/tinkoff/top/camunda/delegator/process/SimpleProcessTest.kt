package ru.tinkoff.top.camunda.delegator.process

import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.externalTask
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.extension.process_test_coverage.junit.rules.ProcessCoverageInMemProcessEngineConfiguration
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutorImpl
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.ResolveArgumentsInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.MultipleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.ContextVariableResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateExecutionResolver
import ru.tinkoff.top.camunda.delegator.example.CamundaJavaDelegate
import ru.tinkoff.top.camunda.delegator.example.KotlinSimpleDelegate
import ru.tinkoff.top.camunda.delegator.parser.DefaultDelegatorBpmnParseFactory

@Deployment(resources = ["processes/simpleProcess.bpmn"])
class SimpleProcessTest {

    private val javaDelegate = spy(ru.tinkoff.top.camunda.delegator.delegates.JavaSimpleDelegate())
    private val kotlinDelegate = spy(KotlinSimpleDelegate())

    @Before
    fun initDelegates() {
        delegateMethodHandlerRegister.initDelegates(
            listOf(javaDelegate, kotlinDelegate)
        )

        Mocks.register("camundaJavaDelegate", CamundaJavaDelegate())
    }

    @Test
    fun testCallDelegateInSimpleProcess() {
        val instance = processEngineRule.runtimeService.startProcessInstanceByKey("simpleProcess")

        assertThat(instance).isStarted
        execute(job("kotlinDelegateActivity"))
        execute(job("simpleJavaDelegateActivity"))
        execute(job("javaDelegateActivity"))
        execute(job("externalTaskActivity"))
        complete(externalTask("externalTaskActivity"))

        instance.hasPassed(
            "kotlinDelegateActivity",
            "javaDelegateActivity",
            "javaDelegateActivity",
            "externalTaskActivity"
        )
            .contains("delegateResultMapping", "delegateResult")
            .contains("javaResultMapping", "valueFromJava")
            .contains("javaDelegateVariableName", "javaDelegateVariableValue")
            .isEnded

        verify(kotlinDelegate).params(eq("requiredValue"), isNull())
        verify(javaDelegate).optionalParams(eq("javaTestParams"), isNull())
    }

    companion object {
        val delegateMethodHandlerRegister = DelegateMethodHandlerMockRegister(
            listOf(
                ResolveArgumentsInterceptor(
                    listOf(
                        ContextVariableResolver(),
                        DelegateExecutionResolver()
                    )
                ),
                SingleResultExecutionWriter(), MultipleResultExecutionWriter()
            ),
            DelegateExecutorImpl()
        )

        private val camundaConfiguration = ProcessCoverageInMemProcessEngineConfiguration().also {
            it.bpmnParseFactory = DefaultDelegatorBpmnParseFactory()
        }

        @Rule
        @ClassRule
        @JvmField
        val processEngineRule: TestCoverageProcessEngineRule = TestCoverageProcessEngineRuleBuilder
            .create(camundaConfiguration.buildProcessEngine())
            .build()
    }
}
