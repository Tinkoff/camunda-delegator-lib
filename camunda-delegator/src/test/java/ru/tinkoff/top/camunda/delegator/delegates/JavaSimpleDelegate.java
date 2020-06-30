package ru.tinkoff.top.camunda.delegator.delegates;

import org.springframework.lang.Nullable;
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate;
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute;
import ru.tinkoff.top.camunda.delegator.delegates.executors.interceptors.output.single.SingleResultVariable;
import ru.tinkoff.top.camunda.delegator.annotations.Variable;
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate;
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute;
import ru.tinkoff.top.camunda.delegator.annotations.Variable;

@CamundaDelegate(name = "javaSimpleDelegate")
public class JavaSimpleDelegate {

    @DelegateExecute
    @SingleResultVariable(name = "javaResult")
    public String optionalParams(
        @Variable(name = "test") String test,
        @Variable(name = "testOptional") @Nullable String testOptional
    ) {
        return "valueFromJava";
    }
}
