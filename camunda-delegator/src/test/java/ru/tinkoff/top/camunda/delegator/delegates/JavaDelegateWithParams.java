package ru.tinkoff.top.camunda.delegator.delegates;

import org.springframework.lang.Nullable;
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate;
import ru.tinkoff.top.camunda.delegator.annotations.DelegateAliases;
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute;
import ru.tinkoff.top.camunda.delegator.annotations.CamundaDelegate;
import ru.tinkoff.top.camunda.delegator.annotations.DelegateAliases;
import ru.tinkoff.top.camunda.delegator.annotations.DelegateExecute;

@CamundaDelegate
public class JavaDelegateWithParams {

    @DelegateExecute
    @DelegateAliases(values = "alias1")
    public void requiredParams(String test) {
    }

    @DelegateExecute
    public void optionalParams(@Nullable String test) {
    }
}
