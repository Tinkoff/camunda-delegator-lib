package ru.tinkoff.top.camunda.delegator.delegates

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate
import ru.tinkoff.top.camunda.delegator.annotations.DelegateAliases
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute
import kotlin.reflect.jvm.javaMethod

class DelegateMetaInformationTest {

    @CamundaDelegate
    @Suppress("UnusedPrivateMember")
    private class TestDelegate {

        @DelegateExecute
        fun test() = Unit

        @DelegateExecute
        @DelegateAliases("other1", "other1", "other3")
        fun testAlias() = Unit

        @DelegateExecute
        fun testWithParams(
            first: String,
            second: Int
        ) = Unit

        @DelegateExecute("")
        fun testEmptyMethodAnnotation() = Unit

        @DelegateExecute("otherMethodName")
        fun testOverrideMethodName() = Unit
    }

    @Test
    fun `check building meta information for delegate`() {
        val meta = DelegateMetaInformation(
            TestDelegate::class.java,
            TestDelegate::test.javaMethod!!
        )

        with(meta) {
            delegateClass shouldBe TestDelegate::class.java

            delegateName shouldBe "testDelegate"
            delegateVersion shouldBe "test"
            delegateFullName shouldBe "testDelegate_test"
            delegateAliases.shouldBeEmpty()

            methodParameters.shouldBeEmpty()
            returnParameter shouldBe MethodParameter(TestDelegate::test.javaMethod!!, -1)
        }
    }

    @Test
    fun `check generating delegate aliases`() {
        val meta = DelegateMetaInformation(
            TestDelegate::class.java,
            TestDelegate::testAlias.javaMethod!!
        )

        with(meta) {
            delegateAliases shouldContainExactlyInAnyOrder hashSetOf("other1", "other3")
        }
    }

    @Test
    fun `check generating delegate method parameters`() {
        val method = TestDelegate::testWithParams.javaMethod!!

        val meta = DelegateMetaInformation(TestDelegate::class.java, method)

        with(meta) {
            methodParameters shouldContainExactly arrayOf(
                MethodParameter(method, 0),
                MethodParameter(method, 1)
            )
        }
    }

    @Test
    fun `when camunda annotation delegate name is empty then delegate name is class name`() {
        val meta = DelegateMetaInformation(
            TestDelegate::class.java,
            TestDelegate::testEmptyMethodAnnotation.javaMethod!!
        )

        with(meta) {
            delegateName shouldBe "testDelegate"
            delegateVersion shouldBe "testEmptyMethodAnnotation"
            delegateFullName shouldBe "testDelegate_testEmptyMethodAnnotation"
            delegateAliases.shouldBeEmpty()
        }
    }

    @Test
    fun `when camunda annotation delegate name is present then override class name`() {

        @CamundaDelegate("otherDelegateName")
        class TestDelegate {

            @DelegateExecute
            fun test() = Unit
        }

        val meta = DelegateMetaInformation(
            TestDelegate::class.java,
            TestDelegate::test.javaMethod!!
        )

        with(meta) {
            delegateName shouldBe "otherDelegateName"
            delegateVersion shouldBe "test"
            delegateFullName shouldBe "otherDelegateName_test"
            delegateAliases.shouldBeEmpty()
        }
    }

    @Test
    fun `when camunda method  annotation delegate name is present then override class name`() {
        val meta = DelegateMetaInformation(
            TestDelegate::class.java, TestDelegate::testOverrideMethodName.javaMethod!!
        )

        with(meta) {
            delegateName shouldBe "testDelegate"
            delegateVersion shouldBe "otherMethodName"
            delegateFullName shouldBe "testDelegate_otherMethodName"
            delegateAliases.shouldBeEmpty()
        }
    }
}
