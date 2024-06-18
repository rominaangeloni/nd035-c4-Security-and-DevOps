package com.wallapop.iam.keycloak

import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Issuer
import com.wallapop.iam.keycloak.TokenResponseAssert.Companion.assertThat
import com.wallapop.iam.oauth2.OAuth2Client
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val CLIENT_ID = "portal-hero"
private const val CLIENT_SECRET = "dSGJlZFLlIZN2ZCIXeW0lAWuq5bnzxHI"

class BasicEndToEnd {
    private val authenticator = KeycloakUserAuthenticator()
    private val confidentialClient =
        FakeConfidentialClient(
            clientId = ClientID(CLIENT_ID),
            clientSecret = Secret(CLIENT_SECRET),
        )
    private val oAuthClient =
        OAuth2Client(
            host = "keycloak",
            port = 9090,
            basePath = "/realms/wallapop-connect",
            authorizationEndpoint = "/protocol/openid-connect/auth",
            tokenEndpoint = "/protocol/openid-connect/token",
        )

    @BeforeEach
    fun setUp() {
        confidentialClient.start()
    }

    @AfterEach
    fun tearDown() {
        authenticator.quit()
        confidentialClient.stop()
    }

    @Test
    fun `a PRO user successfully requests authorization with PKCE and offline_access is the default scope`() =
        runBlocking {
            val authorizationRequestContext =
                oAuthClient.buildRequestContextAuthorizationCodeFlowWithPkce(
                    redirectUri = confidentialClient.redirectUri(),
                    clientId = ClientID(CLIENT_ID),
                )

            confidentialClient.stubRequestReceivedOnRedirectUri("Authorization succeeded")

            authenticator.expectSuccessfulAuthentication(
                credentials =
                    KeycloakUserAuthenticator.Credentials(
                        username = "alice@example.com",
                        password = "12345678",
                    ),
                authenticationUri = authorizationRequestContext.authorizationRequest.toURI(),
                responseExpectedToContain = "Authorization succeeded",
            )

            confidentialClient.verifyRequestReceivedOnRedirectUri(
                state = authorizationRequestContext.state,
                issuer = Issuer(oAuthClient.issuer()),
            )

            assertThat(confidentialClient.exchangeCodeForAccessToken(authorizationRequestContext))
                .accessTokenMatches {
                    assertThat(it.lifetime).isEqualTo(300)
                    assertThat(it.type.toString()).isEqualTo("Bearer")
                    assertThat(it.scope).containsOnly(Scope.Value("offline_access"))
                }
        }

    @Test
    fun `request authorization should fail if the PRO user provide a wrong password`() =
        runBlocking {
            val authorizationRequestContext =
                oAuthClient.buildRequestContextAuthorizationCodeFlowWithPkce(
                    redirectUri = confidentialClient.redirectUri(),
                    clientId = ClientID(CLIENT_ID),
                )

            authenticator.expectUnsuccessfulAuthentication(
                credentials =
                    KeycloakUserAuthenticator.Credentials(
                        username = "alice@example.com",
                        password = "invalidpassword",
                    ),
                authenticationUri = authorizationRequestContext.authorizationRequest.toURI(),
            )

            confidentialClient.verifyRequestNotReceivedOnRedirectUri()
        }

    @Test
    fun `request authorization without PKCE should fail`() =
        runBlocking {
            val authorizationRequestContext =
                oAuthClient.buildRequestContextForAuthorizationCodeFlowWithoutPkce(
                    redirectUri = confidentialClient.redirectUri(),
                    clientId = ClientID(CLIENT_ID),
                )

            authenticator.request(authorizationRequestContext.authorizationRequest.toURI())

            confidentialClient.verifyInvalidRequestReceivedOnRedirectUri(
                state = authorizationRequestContext.state,
                issuer = Issuer(oAuthClient.issuer()),
            )
        }
}
