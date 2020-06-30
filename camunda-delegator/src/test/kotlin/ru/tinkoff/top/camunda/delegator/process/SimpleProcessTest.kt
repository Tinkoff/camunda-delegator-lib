package ru.tinkoff.top.camunda.delegator.process

import org.camunda.bpm.engine.test.Deployment
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
import ru.tinkoff.top.camunda.delegator.delegates.JavaSimpleDelegate
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutorImpl
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.ResolveArgumentsInterceptor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.multiple.MultipleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultExecutionWriter
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.ContextVariableResolver
import ru.tinkoff.top.camunda.delegator.delegates.resolvers.DelegateExecutionResolver
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
    }

    @Test
    fun testCallDelegateInSimpleProcess() {
        val instance = processEngineRule.runtimeService.startProcessInstanceByKey("simpleProcess")

        instance.hasPassed("kotlinDelegateActivity", "javaDelegateActivity")
            .contains("delegateResultMapping", "delegateResult")
            .contains("javaResultMapping", "valueFromJava")
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
}
