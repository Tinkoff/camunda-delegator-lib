pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("build-logic")
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
    "spring:camunda-delegator-spring-boot",
    "spring:camunda-delegator-spring-boot-starter"
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
