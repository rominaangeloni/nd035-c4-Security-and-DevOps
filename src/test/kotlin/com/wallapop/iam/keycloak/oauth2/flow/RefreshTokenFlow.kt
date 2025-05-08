package com.wallapop.iam.keycloak.oauth2.flow

import com.nimbusds.oauth2.sdk.AuthorizationCode
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
import com.wallapop.iam.keycloak.OAuth2Integration
import com.wallapop.iam.keycloak.oauth2.user.auth.KeycloakUserAuthenticator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

abstract class RefreshTokenFlow(override val clientId: String, override val clientSecret: String) :
    OAuth2Integration() {

    @Test
    fun `successfully request authorization using refresh token`() =
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

            val tokenResponse = oAuthClient.exchangeCodeForAccessToken(
                codeVerifier = codeVerifier,
                redirectUri = authorizationRequest.redirectionURI,
                clientId = ClientID(clientId),
                clientSecret = Secret(clientSecret),
                authorizationCode = AuthorizationCode(wireMockServerWrapper.receivedAuthorizationCode()),
            )
            wireMockServerWrapper.verifyTokenReceived(tokenResponse)

            val refreshToken = tokenResponse.toSuccessResponse().tokens.refreshToken

            val refreshTokenResponse = oAuthClient.exchangeRefreshTokenForAccessToken(
                clientId = ClientID(clientId),
                clientSecret = Secret(clientSecret),
                refreshToken = refreshToken,
            )
            wireMockServerWrapper.verifyTokenReceived(refreshTokenResponse)
        }
}
