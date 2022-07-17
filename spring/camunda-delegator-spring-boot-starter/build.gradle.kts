plugins {
    id("tinkoff-library-conventions")
}

dependencies {
    api(project(":spring:camunda-delegator-spring-boot"))
    api(project(":camunda-delegator-test"))
}
