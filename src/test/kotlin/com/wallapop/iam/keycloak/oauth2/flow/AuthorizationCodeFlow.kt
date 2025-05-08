package com.wallapop.iam.keycloak.oauth2.flow

import com.nimbusds.oauth2.sdk.AuthorizationCode
import com.nimbusds.oauth2.sdk.TokenErrorResponse
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
import com.wallapop.iam.keycloak.OAuth2Integration
import com.wallapop.iam.keycloak.oauth2.user.auth.KeycloakUserAuthenticator
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

abstract class AuthorizationCodeFlow(override val clientId: String, override val clientSecret: String) :
    OAuth2Integration() {

    @Test
    fun `a PRO user successfully requests authorization with PKCE and offline_access is the default scope`() =
        runBlocking {
            val codeVerifier = CodeVerifier()
            val authorizationRequest =
                oAuthClient.buildAuthorizationRequestWithPkce(
                    redirectUri = wireMockServerWrapper.redirectUri(),
                    clientId = ClientID(clientId),
                    codeVerifier = codeVerifier,
                )

            wireMockServerWrapper.stubRequestReceivedOnRedirectUri("Authorization succeeded")

            userAuthenticator.expectSuccessfulAuthentication(
                credentials =
                KeycloakUserAuthenticator.Credentials(
                    username = "alice@example.com",
                    password = "12345678",
                ),
                authenticationUri = authorizationRequest.toURI(),
                responseExpectedToContain = "Authorization succeeded",
            )

            wireMockServerWrapper.verifyRequestReceivedOnRedirectUri(
                state = authorizationRequest.state,
                issuer = Issuer(oAuthClient.issuer()),
            )

            val accessToken = oAuthClient.exchangeCodeForAccessToken(
                codeVerifier = codeVerifier,
                redirectUri = authorizationRequest.redirectionURI,
                clientId = ClientID(clientId),
                clientSecret = Secret(clientSecret),
                authorizationCode = AuthorizationCode(wireMockServerWrapper.receivedAuthorizationCode()),
            )
            wireMockServerWrapper.verifyTokenReceived(accessToken)
        }

    @Test
    fun `request authorization should fail if the PRO user provide a wrong password`() =
        runBlocking {
            val codeVerifier = CodeVerifier()
            val authorizationRequest =
                oAuthClient.buildAuthorizationRequestWithPkce(
                    redirectUri = wireMockServerWrapper.redirectUri(),
                    clientId = ClientID(clientId),
                    codeVerifier = codeVerifier,
                )

            userAuthenticator.expectUnsuccessfulAuthentication(
                credentials =
                KeycloakUserAuthenticator.Credentials(
                    username = "alice@example.com",
                    password = "invalidpassword",
                ),
                authenticationUri = authorizationRequest.toURI(),
                responseExpectedToContain = "Invalid username or password",
            )

            wireMockServerWrapper.verifyRequestNotReceivedOnRedirectUri()
        }

    @Test
    fun `request authorization without PKCE should fail`() =
        runBlocking {
            val authorizationRequest =
                oAuthClient.buildAuthorizationRequestWithoutPkce(
                    redirectUri = wireMockServerWrapper.redirectUri(),
                    clientId = ClientID(clientId),
                )

            userAuthenticator.request(authorizationRequest.toURI())

            wireMockServerWrapper.verifyInvalidRequestReceivedOnRedirectUri(
                state = authorizationRequest.state,
                issuer = Issuer(oAuthClient.issuer()),
            )
        }

    @Test
    fun `request authorization with scope different than offline_access`(): Unit =
        runBlocking {
            val authorizationRequest =
                oAuthClient.requestForAuthorizationCodeFlowWithScope(
                    clientId = ClientID(clientId),
                )
            Assertions.assertThat(authorizationRequest.toHTTPResponse().statusCode).isEqualTo(400)
        }

    @Test
    fun `a user cannot be logged in more than 3 times`() =
        runBlocking {
            val codeVerifier = CodeVerifier()
            val authorizationRequest =
                oAuthClient.buildAuthorizationRequestWithPkce(
                    redirectUri = wireMockServerWrapper.redirectUri(),
                    clientId = ClientID(clientId),
                    codeVerifier = codeVerifier,
                )

            wireMockServerWrapper.stubRequestReceivedOnRedirectUri("Authorization succeeded")

            repeat(3) {
                try {
                    userAuthenticator.expectSuccessfulAuthentication(
                        credentials =
                        KeycloakUserAuthenticator.Credentials(
                            username = "kurt@example.com",
                            password = "12345678",
                        ),
                        authenticationUri = authorizationRequest.toURI(),
                        responseExpectedToContain = "Authorization succeeded",
                    )
                    wireMockServerWrapper.verifyRequestReceivedOnRedirectUri(
                        state = authorizationRequest.state,
                        issuer = Issuer(oAuthClient.issuer()),
                    )
                } finally {
                    userAuthenticator.resetDriver()
                }
            }
            userAuthenticator.expectUnsuccessfulAuthentication(
                credentials =
                KeycloakUserAuthenticator.Credentials(
                    username = "kurt@example.com",
                    password = "12345678",
                ),
                authenticationUri = authorizationRequest.toURI(),
                responseExpectedToContain = "You cannot login. Please, contact your Administrator",
            )
            wireMockServerWrapper.verifyRequestReceivedTreeOnRedirectUri()
        }

    @Test
    fun `a default user cannot get authorization`() =
        runBlocking {
            val codeVerifier = CodeVerifier()
            val authorizationRequest =
                oAuthClient.buildAuthorizationRequestWithPkce(
                    redirectUri = wireMockServerWrapper.redirectUri(),
                    clientId = ClientID(clientId),
                    codeVerifier = codeVerifier,
                )

            userAuthenticator.expectUnsuccessfulAuthentication(
                credentials =
                KeycloakUserAuthenticator.Credentials(
                    username = "patrick@example.com",
                    password = "12345678",
                ),
                authenticationUri = authorizationRequest.toURI(),
                responseExpectedToContain = "Invalid username or password",
            )

            wireMockServerWrapper.verifyRequestNotReceivedOnRedirectUri()
        }

    @Test
    fun `request authorization with wrong client-id should fail`(): Unit =
        runBlocking {
            val authorizationRequest =
                oAuthClient.requestForAuthorizationCodeFlowWithScope(
                    clientId = ClientID("wrongclientid"),
                )
            Assertions.assertThat(authorizationRequest.toHTTPResponse().statusCode).isEqualTo(400)
        }

    @Test
    fun `request authorization with wrong client-secret should fail`(): Unit =
        runBlocking {
            val codeVerifier = CodeVerifier()

            val authorizationRequest =
                oAuthClient.buildAuthorizationRequestWithPkce(
                    redirectUri = wireMockServerWrapper.redirectUri(),
                    clientId = ClientID(clientId),
                    codeVerifier = codeVerifier,
                )

            wireMockServerWrapper.stubRequestReceivedOnRedirectUri("Authorization succeeded")

            userAuthenticator.expectSuccessfulAuthentication(
                credentials =
                KeycloakUserAuthenticator.Credentials(
                    username = "bob@example.com",
                    password = "12345678",
                ),
                authenticationUri = authorizationRequest.toURI(),
                responseExpectedToContain = "Authorization succeeded",
            )

            wireMockServerWrapper.verifyRequestReceivedOnRedirectUri(
                state = authorizationRequest.state,
                issuer = Issuer(oAuthClient.issuer()),
            )

            val exchangeCodeRequest = oAuthClient.exchangeCodeForAccessToken(
                codeVerifier = codeVerifier,
                redirectUri = authorizationRequest.redirectionURI,
                clientId = ClientID(clientId),
                clientSecret = Secret("wrongclientsecret"),
                authorizationCode = AuthorizationCode(wireMockServerWrapper.receivedAuthorizationCode()),
            )
            val error = TokenErrorResponse.parse(exchangeCodeRequest.toHTTPResponse()).errorObject
            Assertions.assertThat(error.httpStatusCode).isEqualTo(401)
            Assertions.assertThat(error.code).isEqualTo("unauthorized_client")
            Assertions.assertThat(error.description).isEqualTo("Invalid client or Invalid client credentials")
        }
}
