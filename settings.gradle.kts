pluginManagement {
    repositories {
        //maven("https://plugins.gradle.org/m2/")
        gradlePluginPortal()
        //maven("https://services.gradle.org/distributions/")
        mavenCentral()
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "camunda-delegator-lib"

include(
    "camunda-delegator",
    "camunda-delegator-docs",
    "camunda-delegator-test",
    "camunda-delegator-spring-boot",
    "camunda-delegator-spring-boot-starter"
)

project(":camunda-delegator-spring-boot").projectDir = file("spring/camunda-delegator-spring-boot")
project(":camunda-delegator-spring-boot-starter").projectDir =
    file("spring/camunda-delegator-spring-boot-starter")
