import org.gradle.accessors.dm.LibrariesForLibs


plugins {
    `maven-publish`
    signing

    java
    idea
    jacoco

    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")

    id("io.spring.dependency-management")

    id("org.jetbrains.dokka")
    id("org.jmailen.kotlinter")
}

project.version = resolveVersion()

// https://github.com/gradle/gradle/issues/15383?ysclid=l5pd48d6p8890388000
val libs = the<LibrariesForLibs>()
val springBootVersion: String = libs.versions.springboot.get()

val javadocJarByDokka by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from("$buildDir/dokka/html")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(javadocJarByDokka)
    archives(sourcesJar)
}

tasks {
    test {
        systemProperty(
            "camunda-bpm-process-test-coverage.target-dir-root",
            "build/process-test-coverage/"
        )
        useJUnitPlatform {
            includeEngines("junit-vintage", "junit-jupiter")
        }
    }


    withType<Jar> {
        metaInf.with(
            copySpec {
                from("${project.rootDir}/LICENSE")
            }
        )
    }
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${springBootVersion}")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("Camunda delegator lib")
                description.set("This project provides declarative camunda delegates for Spring based application")
                url.set("https://github.com/TinkoffCreditSystems/camunda-delegator-lib")

                scm {
                    connection.set("scm:git:https://www.github.com/TinkoffCreditSystems/camunda-delegator-lib")
                    developerConnection.set("scm:git:https://github.com/TinkoffCreditSystems/camunda-delegator-lib")
                    url.set("https://github.com/TinkoffCreditSystems/camunda-delegator-lib")
                }

                licenses {
                    license {
                        distribution.set("repo")
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("bespaltovyj")
                        name.set("Pavel Pletnev")
                        email.set("p.pletnev@tinkoff.ru")
                    }
                }
            }


            version = project.version.toString()
            artifactId = project.name
            groupId = "ru.tinkoff.top"

            from(components["java"])

            artifact(javadocJarByDokka)
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    setRequired({
        gradle.taskGraph.hasTask("publish")
    })

    val signingKey: String? by project
    val signingPassword: String? by project

    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications["mavenJava"])
}

// control version of Kotlin
ext["kotlin.version"] = libs.versions.kotlin.get()

dependencies {
    // kotlin
    api(libs.kotlin.stdlib)
    api(libs.kotlin.reflect)
    api(libs.kotlin.logging)

    kapt(libs.spring.boot.configuration.processor)

    dokkaHtmlPlugin(libs.dokka.kotlin.`as`.java)

    testImplementation(libs.h2)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotest.assertions.core.jvm)
    testImplementation(libs.camunda.bpm.assert)
    testImplementation(libs.camunda.bpm.mockito)
    testImplementation(libs.camunda.bpm.process.test.coverage)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.junit.vintage.engine)
}

fun resolveVersion(): String {
    return System.getenv("RELEASE_VERSION") ?: when (val githubRunNumber = System.getenv("GITHUB_RUN_NUMBER")) {
        null -> "LOCAL"
        else -> "GITHUB.${githubRunNumber}-SNAPSHOT"
    }
}
