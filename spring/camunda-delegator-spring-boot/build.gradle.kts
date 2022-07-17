plugins {
    id("tinkoff-library-conventions")
}

dependencies {
    api(project(":camunda-delegator"))
    api(project(":camunda-delegator-docs"))

    api(libs.spring.boot.starter)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.boot.actuator)
    api(libs.spring.boot.actuator.autoconfigure)


    api(libs.camunda.spring.boot.starter)

    kapt(libs.spring.boot.autoconfigure.processor)

    testImplementation(libs.camunda.spring.boot.starter.test)
}
