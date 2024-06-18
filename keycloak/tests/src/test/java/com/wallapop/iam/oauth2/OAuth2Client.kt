package com.wallapop.iam.oauth2

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
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
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

    fun buildRequestContextForAuthorizationCodeFlowWithoutPkce(
        redirectUri: URI,
        clientId: ClientID,
    ): AuthorizationCodeFlowRequestContext {
        val state = State()

        return AuthorizationCodeFlowRequestContext(
            redirectUri = redirectUri,
            state = state,
            authorizationRequest =
                AuthorizationRequest.Builder(
                    ResponseType("code"),
                    NimbusClientId(clientId.value),
                )
                    .endpointURI(
                        URI("http://$host:$port$basePath$authorizationEndpoint"),
                    )
                    .redirectionURI(redirectUri)
                    .state(state)
                    .responseMode(ResponseMode.QUERY)
                    .build(),
        )
    }

    fun buildRequestContextAuthorizationCodeFlowWithPkce(
        redirectUri: URI,
        clientId: ClientID,
    ): AuthorizationCodeFlowWithPkceRequestContext {
        val codeVerifier = CodeVerifier()
        val state = State()

        return AuthorizationCodeFlowWithPkceRequestContext(
            redirectUri = redirectUri,
            state = state,
            codeVerifier = codeVerifier,
            authorizationRequest =
                AuthorizationRequest.Builder(
                    ResponseType("code"),
                    NimbusClientId(clientId.value),
                )
                    .endpointURI(
                        URI("http://$host:$port$basePath$authorizationEndpoint"),
                    )
                    .redirectionURI(redirectUri)
                    .state(state)
                    .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                    .responseMode(ResponseMode.QUERY)
                    .build(),
        )
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

    data class AuthorizationCodeFlowRequestContext(
        val redirectUri: URI,
        val state: State,
        val authorizationRequest: AuthorizationRequest,
    )

    data class AuthorizationCodeFlowWithPkceRequestContext(
        val redirectUri: URI,
        val state: State,
        val codeVerifier: CodeVerifier,
        val authorizationRequest: AuthorizationRequest,
    )
}
