package com.wallapop.iam.keycloak

import com.nimbusds.oauth2.sdk.AuthorizationCode
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant
import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.ResponseMode
import com.nimbusds.oauth2.sdk.ResponseType
import com.nimbusds.oauth2.sdk.TokenRequest
import com.nimbusds.oauth2.sdk.TokenResponse
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URI

// TODO: unify localhost VS keycloak hostnames
private const val AUTH_SERVER_HOST = "http://localhost:9090"
private const val REALM = "wallapop-connect"
private const val CLIENT_ID = "portal-hero"
private const val CLIENT_SECRET = "dSGJlZFLlIZN2ZCIXeW0lAWuq5bnzxHI"
private const val TOKEN_ENDPOINT = "$AUTH_SERVER_HOST/realms/$REALM/protocol/openid-connect/token"

class BasicEndToEnd {
    private val authenticator = KeycloakUserAuthenticator()
    private val fakeConfidentialClientServer = FakeConfidentialClientServer()

    @BeforeEach
    fun setUp() {
        fakeConfidentialClientServer.start()
    }

    @AfterEach
    fun tearDown() {
        authenticator.quit()
        fakeConfidentialClientServer.stop()
    }

    @Test
    fun basicEndToEnd(): Unit =
        runBlocking {
            val authorizationRequestContext = buildAuthenticationRequest(fakeConfidentialClientServer.redirectUri())

            fakeConfidentialClientServer.stubRedirectUriRequestReceived()

            authenticator.expectSuccessfulAuthentication(
                credentials =
                    KeycloakUserAuthenticator.Credentials(
                        username = "alice@example.com",
                        password = "12345678",
                    ),
                authenticationUri = authorizationRequestContext.authorizationRequest.toURI(),
                expectedRedirectResponseContains = "Authorization succeeded",
            )

            fakeConfidentialClientServer.verifyRequestReceivedOnRedirectUri(
                state = authorizationRequestContext.state,
                // TODO: add to Keycloak client
                issuer = Issuer("http://keycloak:9090/realms/wallapop-connect"),
            )

            val tokenResponse =
                exchangeCodeForAccessToken(
                    codeVerifier = authorizationRequestContext.codeVerifier,
                    redirectUri = authorizationRequestContext.redirectUri,
                    clientID = ClientID(CLIENT_ID),
                    clientSecret = Secret(CLIENT_SECRET),
                    authorizationCode = AuthorizationCode(fakeConfidentialClientServer.receivedAuthorizationCode()),
                )

            if (!tokenResponse.indicatesSuccess()) {
                fail<String>("Access token request failed: ${tokenResponse.toErrorResponse().errorObject}")
            }
            tokenResponse.toSuccessResponse().tokens.accessToken.let {
                assertNotNull(it)
                assertEquals(300, it.lifetime)
                assertEquals("Bearer", it.type.toString())
                assertTrue(it.scope.toStringList().size == 1)
                assertTrue(it.scope.contains("offline_access"))
            }
        }

    private fun buildAuthenticationRequest(redirectUri: URI): AuthorizationRequestContext {
        val codeVerifier = CodeVerifier()
        val state = State()

        return AuthorizationRequestContext(
            redirectUri = redirectUri,
            state = state,
            codeVerifier = codeVerifier,
            authorizationRequest =
                AuthorizationRequest.Builder(
                    ResponseType("code"),
                    ClientID("portal-hero"),
                )
                    .endpointURI(
                        URI("http://keycloak:9090/realms/wallapop-connect/protocol/openid-connect/auth"),
                    ) // TODO: add to Keycloak client
                    .redirectionURI(redirectUri)
                    .state(state)
                    .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                    .responseMode(ResponseMode.QUERY)
                    .build(),
        )
    }

    data class AuthorizationRequestContext(
        val redirectUri: URI,
        val state: State,
        val codeVerifier: CodeVerifier,
        val authorizationRequest: AuthorizationRequest,
    )

    // TODO: reduce amount of arguments
    private fun exchangeCodeForAccessToken(
        codeVerifier: CodeVerifier,
        redirectUri: URI,
        clientID: ClientID,
        clientSecret: Secret,
        authorizationCode: AuthorizationCode,
    ): TokenResponse {
        val codeGrant = AuthorizationCodeGrant(authorizationCode, redirectUri, codeVerifier)
        val clientAuth: ClientAuthentication = ClientSecretBasic(clientID, clientSecret)
        val tokenEndpoint = URI(TOKEN_ENDPOINT)

        val request = TokenRequest(tokenEndpoint, clientAuth, codeGrant)
        return TokenResponse.parse(request.toHTTPRequest().send())
    }
}
