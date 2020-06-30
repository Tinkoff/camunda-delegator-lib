plugins {
    id("kotlin")
}

dependencies {
    api(project(":camunda-delegator"))
    api(group = "org.springdoc", name = "springdoc-openapi-kotlin", version = "1.5.10")
    api(group = "org.springframework.boot", name = "spring-boot-actuator")
}
