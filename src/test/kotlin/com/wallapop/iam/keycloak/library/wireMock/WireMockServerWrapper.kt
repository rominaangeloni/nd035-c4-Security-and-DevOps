package com.wallapop.iam.keycloak.library.wireMock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.absent
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.not
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.TokenResponse
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.token.AccessToken
import com.wallapop.iam.keycloak.library.wireMock.TokenResponseAssert.Companion.assertToken
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import java.net.URI

class WireMockServerWrapper {
    private val host = resolveDockerHostname()
    private val port = 9876
    private val redirectUriPath = "/callback"

    private val wireMockServer = WireMockServer(port)

    init {
        configureFor(port)
    }

    fun start() = wireMockServer.start()

    fun stop() = wireMockServer.stop()

    private fun resolveDockerHostname() =
        when {
            System.getProperty("os.name").contains("Mac") -> "host.docker.internal"
            else -> "172.17.0.1"
        }

    fun redirectUri(): URI = URI.create("http://$host:$port$redirectUriPath")

    fun stubRequestReceivedOnRedirectUri(responseBody: String) {
        stubFor(
            get(urlPathEqualTo(redirectUriPath))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(responseBody),
                ),
        )
    }

    fun verifyRequestReceivedOnRedirectUri(
        state: State,
        issuer: Issuer,
    ) {
        verify(
            getRequestedFor(urlPathEqualTo(redirectUriPath))
                .withQueryParam("state", equalTo(state.toString()))
                .withQueryParam("session_state", not(absent()))
                .withQueryParam("iss", equalTo(issuer.value))
                .withQueryParam("code", matching(".+\\..+\\..+")),
        )
    }

    fun verifyInvalidRequestReceivedOnRedirectUri(
        state: State,
        issuer: Issuer,
    ) {
        verify(
            getRequestedFor(urlPathEqualTo(redirectUriPath))
                .withQueryParam("state", equalTo(state.toString()))
                .withQueryParam("iss", equalTo(issuer.value))
                .withQueryParam("error", equalTo("invalid_request"))
                .withQueryParam("error_description", equalTo("Missing parameter: code_challenge_method")),
        )
    }

    fun verifyRequestNotReceivedOnRedirectUri() {
        verify(
            exactly(0),
            getRequestedFor(urlPathEqualTo(redirectUriPath)),
        )
    }

    fun verifyRequestReceivedTreeOnRedirectUri() {
        verify(
            exactly(3),
            getRequestedFor(urlPathEqualTo(redirectUriPath)),
        )
    }

    private fun findFirstServeEventWithUrlStartingWith() =
        getAllServeEvents().first {
            it.request.url.startsWith(redirectUriPath)
        }

    fun receivedAuthorizationCode(): String =
        findFirstServeEventWithUrlStartingWith()
            .request.queryParams["code"]!!.values().first()

    fun verifyTokenReceived(accessToken: TokenResponse) {
        assertToken(accessToken)
            .accessTokenMatches {
                assertThat(it.lifetime).isEqualTo(300)
                assertThat(it.type.toString()).isEqualTo("Bearer")
                assertThat(it.scope).containsOnly(Scope.Value("offline_access"))
            }
    }
}

class TokenResponseAssert(actual: TokenResponse) : AbstractAssert<TokenResponseAssert, TokenResponse>(
    actual,
    TokenResponseAssert::class.java,
) {
    companion object {
        fun assertToken(locale: TokenResponse) = TokenResponseAssert(locale)
    }

    fun accessTokenMatches(block: (AccessToken) -> Unit) = block(this.actual.toSuccessResponse().tokens.accessToken)
}
