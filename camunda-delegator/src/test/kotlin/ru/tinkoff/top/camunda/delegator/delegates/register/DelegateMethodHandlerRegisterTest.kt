package ru.tinkoff.top.camunda.delegator.delegates.register

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateAliases
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import ru.tinkoff.top.camunda.delegator.delegates.DelegateInformation
import ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams
import ru.tinkoff.top.camunda.delegator.delegates.executors.DelegateExecutor
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.DelegateInterceptor

class DelegateMethodHandlerRegisterTest {

    private val mainDelegateExecutor: DelegateExecutor = mock()

    @CamundaDelegate
    private class TestDelegate {

        @DelegateExecute
        fun test() = Unit

        @DelegateExecute
        fun second() = Unit
    }

    @Test
    fun `check register delegates`() {

        val register = DelegateMethodHandlerRegister(emptyList(), mainDelegateExecutor)
        register.initDelegates(listOf(TestDelegate()))

        val firstDelegate = register.getDelegateMethodHandler("testDelegate_test")
        firstDelegate shouldNotBe null

        with(firstDelegate!!.delegateInformation.metaInformation) {
            delegateClass shouldBe TestDelegate::class.java

            delegateName shouldBe "testDelegate"
            delegateVersion shouldBe "test"
            delegateFullName shouldBe "testDelegate_test"
            delegateAliases.shouldBeEmpty()
        }

        val secondDelegate = register.getDelegateMethodHandler("testDelegate_second")
        secondDelegate shouldNotBe null

        with(secondDelegate!!.delegateInformation.metaInformation) {
            delegateClass shouldBe TestDelegate::class.java

            delegateName shouldBe "testDelegate"
            delegateVersion shouldBe "second"
            delegateFullName shouldBe "testDelegate_second"
            delegateAliases.shouldBeEmpty()
        }
    }

    @Test
    fun `check register java delegates`() {

        val register = DelegateMethodHandlerRegister(emptyList(), mainDelegateExecutor)
        register.initDelegates(listOf(ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams()))

        val firstDelegate = register.getDelegateMethodHandler("javaDelegateWithParams_requiredParams")
        firstDelegate shouldNotBe null

        with(firstDelegate!!.delegateInformation.metaInformation) {
            delegateClass shouldBe ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams::class.java

            delegateName shouldBe "javaDelegateWithParams"
            delegateVersion shouldBe "requiredParams"
            delegateFullName shouldBe "javaDelegateWithParams_requiredParams"
            delegateAliases.shouldContainExactly(hashSetOf("alias1"))
        }

        val secondDelegate = register.getDelegateMethodHandler("javaDelegateWithParams_optionalParams")
        secondDelegate shouldNotBe null

        with(secondDelegate!!.delegateInformation.metaInformation) {
            delegateClass shouldBe ru.tinkoff.top.camunda.delegator.delegates.JavaDelegateWithParams::class.java

            delegateName shouldBe "javaDelegateWithParams"
            delegateVersion shouldBe "optionalParams"
            delegateFullName shouldBe "javaDelegateWithParams_optionalParams"
            delegateAliases.shouldBeEmpty()
        }
    }

    @CamundaDelegate
    class TestDelegateAliases {

        @DelegateExecute
        @DelegateAliases("alias1", "alias2")
        fun test() = Unit
    }

    @Test
    fun `when delegate aliases exists then also register by alias`() {
        val register = DelegateMethodHandlerRegister(emptyList(), mainDelegateExecutor)
        register.initDelegates(listOf(TestDelegateAliases()))

        val delegateByFullName = register.getDelegateMethodHandler("testDelegateAliases_test")
        delegateByFullName shouldNotBe null

        with(delegateByFullName!!.delegateInformation.metaInformation) {
            delegateClass shouldBe TestDelegateAliases::class.java

            delegateName shouldBe "testDelegateAliases"
            delegateVersion shouldBe "test"
            delegateFullName shouldBe "testDelegateAliases_test"
            delegateAliases shouldContainExactlyInAnyOrder hashSetOf("alias1", "alias2")
        }

        val delegateByFirstAlias = register.getDelegateMethodHandler("alias1")
        delegateByFirstAlias shouldNotBe null

        val delegateBySecondAlias = register.getDelegateMethodHandler("alias2")
        delegateBySecondAlias shouldNotBe null

        assertEquals(delegateByFullName, delegateByFirstAlias)
        assertEquals(delegateByFirstAlias, delegateBySecondAlias)
    }

    @CamundaDelegate
    private class TestDelegate1 {

        @DelegateExecute
        fun test() = Unit
    }

    @CamundaDelegate
    private class TestDelegate2 {

        @DelegateExecute
        fun test() = Unit
    }

    @Test
    fun `when delegate exists in 2 class then register both`() {
        val register = DelegateMethodHandlerRegister(emptyList(), mainDelegateExecutor)
        register.initDelegates(listOf(TestDelegate1(), TestDelegate2()))

        val firstDelegate = register.getDelegateMethodHandler("testDelegate1_test")
        firstDelegate shouldNotBe null

        val secondDelegate = register.getDelegateMethodHandler("testDelegate2_test")
        secondDelegate shouldNotBe null
    }

    @CamundaDelegate("testDelegate")
    private class TestDelegate3 {
        @DelegateExecute
        fun test() = Unit
    }

    @CamundaDelegate("testDelegate")
    private class TestDelegate4 {
        @DelegateExecute
        fun test() = Unit
    }

    @Test
    fun `when delegate exists in 2 class then registration error`() {
        val register = DelegateMethodHandlerRegister(emptyList(), mainDelegateExecutor)
        val error = assertThrows<IllegalArgumentException> {
            register.initDelegates(listOf(TestDelegate3(), TestDelegate4()))
        }

        error.message shouldStartWith "Delegate testDelegate with version test defined twice."
    }

    @CamundaDelegate
    class TestDelegate5 {
        @DelegateExecute
        @DelegateAliases("testalias")
        fun test1() = Unit
    }

    @CamundaDelegate
    class TestDelegate6 {
        @DelegateExecute
        @DelegateAliases("testalias")
        fun test2() = Unit
    }

    @Test
    fun `when same alias exists in 2 delegates then registration error`() {
        val register = DelegateMethodHandlerRegister(emptyList(), mainDelegateExecutor)
        val error = assertThrows<IllegalArgumentException> {
            register.initDelegates(listOf(TestDelegate5(), TestDelegate6()))
        }

        error.message shouldStartWith "Alias testalias used twice."
    }

    @CamundaDelegate
    private class TestDelegateInterceptor {
        @DelegateExecute
        fun test() = Unit
    }

    @Test
    fun `when delegate interceptor not present then chain not building`() {
        val register = DelegateMethodHandlerRegister(emptyList(), mainDelegateExecutor)
        register.initDelegates(listOf(TestDelegateInterceptor()))

        val firstDelegate = register.getDelegateMethodHandler("testDelegateInterceptor_test")
        firstDelegate!!.execute(mock())

        verify(mainDelegateExecutor).execute(any(), any(), isNull())
    }

    @Test
    fun `when delegate interceptor present then chain building`() {
        open class SimpleInterceptor : DelegateInterceptor() {
            override fun execute(
                execution: DelegateExecution,
                delegateInfo: DelegateInformation,
                delegateArguments: Array<Any?>?
            ): Any? {
                return nextExecutor.execute(execution, delegateInfo, delegateArguments)
            }
        }
        val spyInterceptor = spy(SimpleInterceptor())

        val register = DelegateMethodHandlerRegister(listOf(spyInterceptor), mainDelegateExecutor)
        register.initDelegates(listOf(TestDelegateInterceptor()))

        val firstDelegate = register.getDelegateMethodHandler("testDelegateInterceptor_test")
        firstDelegate!!.execute(mock())

        verify(mainDelegateExecutor).execute(any(), any(), isNull())
    }
}
