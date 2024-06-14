import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    jacoco
}

group = "com.wallapop.iam.keycloak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val junit = "5.10.2"
    val selenium = "4.21.0"
    val nimbusdsOauthClient = "11.12"
    val keycloakAdminClient = "25.0.0"
    val wiremock = "3.6.0"
    val ktor = "2.3.11"

    testImplementation(platform("org.junit:junit-bom:$junit"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.wiremock:wiremock:$wiremock")
    testImplementation("org.seleniumhq.selenium:selenium-java:$selenium")
    testImplementation("com.nimbusds:oauth2-oidc-sdk:$nimbusdsOauthClient")
    testImplementation("org.keycloak:keycloak-admin-client:$keycloakAdminClient")
    testImplementation("io.ktor:ktor-client-core:$ktor")
    testImplementation("io.ktor:ktor-client-cio:$ktor")
    testImplementation("io.ktor:ktor-client-auth:$ktor")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor")
    testImplementation("io.ktor:ktor-serialization-jackson:$ktor")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

ktlint {
    verbose.set(true)
    enableExperimentalRules.set(true)
}
