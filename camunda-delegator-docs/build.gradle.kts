plugins {
    id("tinkoff-library-conventions")
}

dependencies {
    api(project(":camunda-delegator"))
    api(libs.springdoc.kotlin)
    api(libs.spring.boot.actuator)
}
