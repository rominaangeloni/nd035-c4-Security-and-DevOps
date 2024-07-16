package com.wallapop.iam.keycloak.oauth2.user.auth

import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL
import java.time.Duration

private val logger = LoggerFactory.getLogger(KeycloakUserAuthenticator::class.java)

class KeycloakUserAuthenticator {
    private var driver = RemoteWebDriver(URL("http://localhost:4444/"), ChromeOptions())

    fun request(authenticationUri: URI) {
        driver.get(authenticationUri.toString())
    }

    fun expectSuccessfulAuthentication(
        credentials: Credentials,
        authenticationUri: URI,
        responseExpectedToContain: String,
    ) {
        tryAuthenticate(
            credentials = credentials,
            authenticationUri = authenticationUri,
            responseExpectedToContain = responseExpectedToContain,
        )
    }

    private fun tryAuthenticate(
        credentials: Credentials,
        authenticationUri: URI,
        responseExpectedToContain: String,
    ) {
        request(authenticationUri)

        driver.findElement(By.id("username"))
            .sendKeys(credentials.username)
        driver.findElement(By.id("password"))
            .sendKeys(credentials.password)
        driver.findElement(By.id("kc-login"))
            .submit()

        WebDriverWait(driver, Duration.ofMillis(1000))
            .until {
                logger.debug("Keycloak authentication response: ${it.pageSource}")
                it.pageSource.contains(responseExpectedToContain)
            }
    }

    fun expectUnsuccessfulAuthentication(
        credentials: Credentials,
        authenticationUri: URI,
        responseExpectedToContain: String,
    ) {
        tryAuthenticate(
            credentials = credentials,
            authenticationUri = authenticationUri,
            responseExpectedToContain = responseExpectedToContain,
        )
    }

    fun resetDriver() {
        initializeDriver()
    }
    private fun initializeDriver() {
        driver.quit()
        driver = RemoteWebDriver(URL("http://localhost:4444/"), ChromeOptions())
    }

    fun quit() = driver.quit()

    data class Credentials(
        val username: String,
        val password: String,
    )
}
