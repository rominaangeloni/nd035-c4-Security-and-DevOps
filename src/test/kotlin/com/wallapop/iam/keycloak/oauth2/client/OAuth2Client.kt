package com.wallapop.iam.keycloak.oauth2.client

import com.nimbusds.oauth2.sdk.AuthorizationCode
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant
import com.nimbusds.oauth2.sdk.AuthorizationGrant
import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.RefreshTokenGrant
import com.nimbusds.oauth2.sdk.ResponseMode
import com.nimbusds.oauth2.sdk.ResponseType
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.TokenRequest
import com.nimbusds.oauth2.sdk.TokenResponse
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
import com.nimbusds.oauth2.sdk.token.RefreshToken
import java.net.URI
import com.nimbusds.oauth2.sdk.AuthorizationCode as NimbusAuthorizationCode
import com.nimbusds.oauth2.sdk.auth.Secret as NimbusSecret
import com.nimbusds.oauth2.sdk.id.ClientID as NimbusClientId

class OAuth2Client(
    private val host: String,
    private val port: Int,
    private val basePath: String,
    private val authorizationEndpoint: String,
    private val tokenEndpoint: String,
) {
    fun issuer() = "http://$host:$port$basePath"

    fun requestForAuthorizationCodeFlowWithScope(
        clientId: ClientID,
    ): TokenResponse {
        val state = State()

        val request = AuthorizationRequest.Builder(
            ResponseType("code"),
            NimbusClientId(clientId.value),
        )
            .endpointURI(
                URI("http://$host:$port$basePath$authorizationEndpoint"),
            )
            .state(state)
            .responseMode(ResponseMode.QUERY)
            .scope(Scope.parse("email"))
            .build()

        return TokenResponse.parse(request.toHTTPRequest().send())
    }

    private fun buildAuthorizationRequest(
        redirectUri: URI,
        clientId: ClientID,
        codeVerifier: CodeVerifier? = null,
    ): AuthorizationRequest.Builder {
        val state = State()

        val builder = AuthorizationRequest.Builder(
            ResponseType("code"),
            NimbusClientId(clientId.value),
        )
            .endpointURI(URI("http://$host:$port$basePath$authorizationEndpoint"))
            .redirectionURI(redirectUri)
            .state(state)
            .responseMode(ResponseMode.QUERY)

        return builder
    }

    fun buildAuthorizationRequestWithPkce(
        redirectUri: URI,
        clientId: ClientID,
        codeVerifier: CodeVerifier,
    ): AuthorizationRequest {
        val builder = buildAuthorizationRequest(redirectUri, clientId, codeVerifier)
        builder.codeChallenge(codeVerifier, CodeChallengeMethod.S256)
        return builder.build()
    }

    fun buildAuthorizationRequestWithoutPkce(
        redirectUri: URI,
        clientId: ClientID,
    ): AuthorizationRequest {
        val builder = buildAuthorizationRequest(redirectUri, clientId)
        return builder.build()
    }

    fun exchangeCodeForAccessToken(
        codeVerifier: CodeVerifier,
        redirectUri: URI,
        clientId: ClientID,
        clientSecret: Secret,
        authorizationCode: AuthorizationCode,
    ): TokenResponse {
        val codeGrant =
            AuthorizationCodeGrant(
                NimbusAuthorizationCode(authorizationCode.value),
                redirectUri,
                codeVerifier,
            )
        val clientAuth: ClientAuthentication =
            ClientSecretBasic(
                NimbusClientId(clientId.value),
                NimbusSecret(clientSecret.value),
            )
        val tokenEndpoint = URI("http://$host:$port$basePath$tokenEndpoint")

        val request = TokenRequest(tokenEndpoint, clientAuth, codeGrant)
        return TokenResponse.parse(request.toHTTPRequest().send())
    }

    fun exchangeRefreshTokenForAccessToken(
        clientId: ClientID,
        clientSecret: Secret,
        refreshToken: RefreshToken,
    ): TokenResponse {
        val clientAuth: ClientAuthentication = ClientSecretPost(clientId, clientSecret)
        val refreshTokenGrant: AuthorizationGrant = RefreshTokenGrant(refreshToken)
        val parameters: MutableMap<String, MutableList<String>> = mutableMapOf(
            "grant_type" to mutableListOf("refresh_token"),
            "auth-method" to mutableListOf(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.value),
        )
        val tokenRequest = TokenRequest(
            URI("http://$host:$port$basePath$tokenEndpoint"),
            clientAuth,
            refreshTokenGrant,
            Scope.parse(""),
            null,
            parameters,
        )
        return TokenResponse.parse(tokenRequest.toHTTPRequest().send())
    }
}
