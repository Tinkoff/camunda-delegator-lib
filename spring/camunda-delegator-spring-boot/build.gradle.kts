plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    api(project(":camunda-delegator"))
    api(project(":camunda-delegator-docs"))

    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-actuator")
    api("org.springframework.boot:spring-boot-actuator-autoconfigure")

    api("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter:${Versions.CAMUNDA}")

    kapt("org.springframework.boot:spring-boot-autoconfigure-processor")
}
