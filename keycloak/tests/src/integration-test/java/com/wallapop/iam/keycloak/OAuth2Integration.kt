package com.wallapop.iam.keycloak

import com.wallapop.iam.keycloak.library.wireMock.WireMockServerWrapper
import com.wallapop.iam.keycloak.oauth2.client.OAuth2Client
import com.wallapop.iam.keycloak.oauth2.user.auth.KeycloakUserAuthenticator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class OAuth2Integration {

    protected var userAuthenticator = KeycloakUserAuthenticator()
    protected val wireMockServerWrapper = WireMockServerWrapper()
    protected val oAuthClient =
        OAuth2Client(
            host = "keycloak",
            port = 9090,
            basePath = "/realms/wallapop-connect",
            authorizationEndpoint = "/protocol/openid-connect/auth",
            tokenEndpoint = "/protocol/openid-connect/token",
        )

    abstract val clientId: String
    abstract val clientSecret: String

    @BeforeEach
    fun setUp() {
        wireMockServerWrapper.start()
    }

    @AfterEach
    fun tearDown() {
        userAuthenticator.quit()
        wireMockServerWrapper.stop()
    }
}
