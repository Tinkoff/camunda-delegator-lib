plugins {
    id("tinkoff-library-conventions")
}

dependencies {
    api(libs.camunda.engine)
    api(libs.camunda.engine.spring)

    api(libs.spring.context)
}
