package com.wallapop.iam.keycloak

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.absent
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.not
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.oauth2.sdk.id.State
import java.net.URI

class FakeConfidentialClientServer() {
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

    fun stubRedirectUriRequestReceived() {
        stubFor(
            get(urlPathEqualTo(redirectUriPath))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("Authorization succeeded"),
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
                .withQueryParam("iss", equalTo(issuer.toString()))
                .withQueryParam("code", matching(".+\\..+\\..+")),
        )
    }

    fun receivedAuthorizationCode(): String =
        findFirstServeEventWithUrlStartingWith()
            .request.queryParams["code"]!!.values().first()

    private fun findFirstServeEventWithUrlStartingWith() =
        getAllServeEvents().first {
            it.request.url.startsWith(redirectUriPath)
        }
}
