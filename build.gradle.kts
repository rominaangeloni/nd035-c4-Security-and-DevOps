import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.diffplug.spotless") version "6.25.0"
    jacoco
}

group = "com.wallapop.iam.keycloak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val kotlinCoroutines = "1.8.1"
    val logback = "1.5.6"
    val junit = "5.10.2"
    val assertj = "3.25.1"
    val selenium = "4.21.0"
    val nimbusdsOauthClient = "11.12"
    val wiremock = "3.6.0"

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutines")
    testImplementation("ch.qos.logback:logback-classic:$logback")
    testImplementation(platform("org.junit:junit-bom:$junit"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:$assertj")
    testImplementation("org.wiremock:wiremock:$wiremock")
    testImplementation("org.seleniumhq.selenium:selenium-java:$selenium")
    testImplementation("com.nimbusds:oauth2-oidc-sdk:$nimbusdsOauthClient")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.getByName("classes").dependsOn(tasks.getByName("spotlessApply"))

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    isEnforceCheck = false

    kotlin {
        ktlint("0.50.0")
    }
}
