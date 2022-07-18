plugins {
    kotlin("jvm") version libs.versions.kotlin.get()

    id("com.github.jk1.dependency-license-report") version "1.17"
    id("org.jmailen.kotlinter") version libs.versions.kotlinter.get()
    id("org.jetbrains.dokka") version libs.versions.kotlin.get()

    id("org.jetbrains.kotlinx.binary-compatibility-validator") version libs.versions.kotvalidator.get()
}

tasks {
    check {
        dependsOn(installKotlinterPrePushHook, checkLicense)
    }

    dokkaHtmlMultiModule {
        outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
    }

}

licenseReport {
    excludeBoms = true
    allowedLicensesFile = File("$projectDir/config/license-allowed.json")
    filters = arrayOf<com.github.jk1.license.filter.DependencyFilter>(
        com.github.jk1.license.filter.LicenseBundleNormalizer(
            "$projectDir/config/license-normalizer-bundle.json", false
        )
    )
}
