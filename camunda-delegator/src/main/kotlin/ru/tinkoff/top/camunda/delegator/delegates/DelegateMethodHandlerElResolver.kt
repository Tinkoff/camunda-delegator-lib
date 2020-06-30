package ru.tinkoff.top.camunda.delegator.delegates

import org.camunda.bpm.engine.ProcessEngineException
import org.camunda.bpm.engine.impl.javax.el.ELContext
import org.camunda.bpm.engine.impl.javax.el.ELResolver
import ru.tinkoff.top.camunda.delegator.delegates.register.DelegateExecutorsRegister
import java.beans.FeatureDescriptor

class DelegateMethodHandlerElResolver(
    private val delegateMethodHandlerRegister: DelegateExecutorsRegister
) : ELResolver() {

    override fun getValue(context: ELContext?, base: Any?, property: Any?): Any? {
        // according to javadoc, can only be a String
        val key = property as String
        return delegateMethodHandlerRegister.getDelegateMethodHandler(key)?.also {
            context!!.isPropertyResolved = true
        }
    }

    override fun setValue(context: ELContext?, base: Any?, property: Any?, value: Any?) {
        throw ProcessEngineException(
            "Cannot set value of ' $property ', " +
                "it resolves to a bean defined in the Spring application-context."
        )
    }

    override fun getCommonPropertyType(context: ELContext?, base: Any?): Class<*> {
        return Any::class.java
    }

    override fun getType(context: ELContext?, base: Any?, property: Any?): Class<*> {
        return Any::class.java
    }

    override fun getFeatureDescriptors(context: ELContext?, base: Any?): MutableIterator<FeatureDescriptor>? {
        return null
    }

    override fun isReadOnly(context: ELContext?, base: Any?, property: Any?): Boolean {
        return true
    }
}
