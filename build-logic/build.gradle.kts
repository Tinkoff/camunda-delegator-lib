plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.allopen)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kotlinter.gradle)

    // https://github.com/gradle/gradle/issues/15383?ysclid=l5pd48d6p8890388000
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.spring.dependency.management.plugin)
}
