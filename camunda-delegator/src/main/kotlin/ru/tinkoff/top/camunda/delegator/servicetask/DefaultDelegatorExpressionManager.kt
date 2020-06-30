package ru.tinkoff.top.camunda.delegator.servicetask

import org.camunda.bpm.engine.impl.el.ExpressionManager
import org.camunda.bpm.engine.impl.el.VariableContextElResolver
import org.camunda.bpm.engine.impl.el.VariableScopeElResolver
import org.camunda.bpm.engine.impl.javax.el.ArrayELResolver
import org.camunda.bpm.engine.impl.javax.el.BeanELResolver
import org.camunda.bpm.engine.impl.javax.el.CompositeELResolver
import org.camunda.bpm.engine.impl.javax.el.ListELResolver
import org.camunda.bpm.engine.impl.javax.el.MapELResolver
import org.camunda.bpm.engine.spring.ApplicationContextElResolver
import org.springframework.context.ApplicationContext
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMethodHandlerElResolver
import ru.tinkoff.top.camunda.delegator.delegates.register.DelegateExecutorsRegister

/**
 *
 * ExpressionManager with {@link DelegateMethodHandlerElResolver}
 *
 * @see DelegateExecutorsRegister
 * @see DelegateMethodHandlerElResolver
 *
 * @author p.pletnev
 */
class DefaultDelegatorExpressionManager(
    private val applicationContext: ApplicationContext,
    private val delegateMethodHandlerRegister: DelegateExecutorsRegister
) : ExpressionManager() {

    override fun createElResolver(): CompositeELResolver {
        val compositeElResolver = CompositeELResolver()
        compositeElResolver.add(VariableScopeElResolver())
        compositeElResolver.add(VariableContextElResolver())

        compositeElResolver.add(DelegateMethodHandlerElResolver(delegateMethodHandlerRegister))
        compositeElResolver.add(ApplicationContextElResolver(applicationContext))

        compositeElResolver.add(ArrayELResolver())
        compositeElResolver.add(ListELResolver())
        compositeElResolver.add(MapELResolver())
        compositeElResolver.add(BeanELResolver())

        return compositeElResolver
    }
}
