plugins {
    `maven-publish`
    signing

    kotlin("jvm") version Versions.KOTLIN
    kotlin("kapt") version Versions.KOTLIN
    kotlin("plugin.allopen") version Versions.KOTLIN apply false

    id("io.gitlab.arturbosch.detekt") version Versions.DETEKT

    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    id("com.github.jk1.dependency-license-report") version "1.17"
    id("org.jmailen.kotlinter") version "3.6.0"
    id("org.jetbrains.dokka") version "1.5.0"
}


val camundaVersion = Versions.CAMUNDA
val springBootVersion = "2.5.2"
val configDir: File by extra { rootProject.projectDir.toPath().resolve("config").toFile() }

tasks {
    check {
        dependsOn(installKotlinterPrePushHook)
    }

    dokkaHtmlMultiModule {
        outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
    }
}

licenseReport {
    allowedLicensesFile = File("$projectDir/config/license-allowed.json")
    filters = arrayOf<com.github.jk1.license.filter.DependencyFilter>(
        com.github.jk1.license.filter.LicenseBundleNormalizer(
            "$projectDir/config/license-normalizer-bundle.json", false
        )
    )
}

allprojects {
    repositories {
        mavenCentral()
    }

    version = CI.version
}


subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "kotlin-spring")

    apply(plugin = "io.spring.dependency-management")

    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "jacoco")

    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")


    apply(plugin = "org.jmailen.kotlinter")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        config = files("$configDir/detekt.yml")
        autoCorrect = true
        source = files("src/main/kotlin")
    }

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
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        withType<io.gitlab.arturbosch.detekt.Detekt> {
            dependsOn(formatKotlin)
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


                version = version
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
    ext["kotlin.version"] = Versions.KOTLIN


    dependencies {
        // kotlin
        api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        api("org.jetbrains.kotlin:kotlin-reflect")
        api("io.github.microutils:kotlin-logging:2.0.11")

        // camunda
        api("org.camunda.bpm:camunda-engine:$camundaVersion")
        api("org.camunda.bpm:camunda-engine-spring:$camundaVersion")

        kapt("org.springframework.boot:spring-boot-configuration-processor")

        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}")
        dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.0")


        testImplementation("com.h2database:h2")
        testImplementation("org.assertj:assertj-core")
        testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
        testImplementation("org.camunda.bpm:camunda-engine:$camundaVersion")
        testImplementation("org.camunda.bpm:camunda-engine-spring:$camundaVersion")
        testImplementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-test:$camundaVersion")
        testImplementation("org.camunda.bpm.assert:camunda-bpm-assert:13.0.0")
        testImplementation("org.camunda.bpm.extension.mockito:camunda-bpm-mockito:5.16.0")
        testImplementation("org.camunda.bpm.extension:camunda-bpm-process-test-coverage:0.4.0")
        testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.3")


        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api")
        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine")
        testImplementation(group = "org.junit.vintage", name = "junit-vintage-engine")
    }
}
