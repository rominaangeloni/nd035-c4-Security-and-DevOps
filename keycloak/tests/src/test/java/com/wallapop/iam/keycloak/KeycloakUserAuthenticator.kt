package com.wallapop.iam.keycloak

import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URI
import java.net.URL
import java.time.Duration

class KeycloakUserAuthenticator {
    private val driver = RemoteWebDriver(URL("http://localhost:4444/"), ChromeOptions())

    fun expectSuccessfulAuthentication(
        credentials: Credentials,
        authenticationUri: URI,
        expectedRedirectResponseContains: String,
    ) {
        driver.get(authenticationUri.toString())

        driver.findElement(By.id("username"))
            .sendKeys(credentials.username)
        driver.findElement(By.id("password"))
            .sendKeys(credentials.password)
        driver.findElement(By.id("kc-login"))
            .submit()

        WebDriverWait(driver, Duration.ofMillis(1000))
            .until {
                println("Redirect response: ${it.pageSource}")
                it.pageSource.contains(expectedRedirectResponseContains)
            }
    }

    fun quit() = driver.quit()

    data class Credentials(
        val username: String,
        val password: String,
    )
}
