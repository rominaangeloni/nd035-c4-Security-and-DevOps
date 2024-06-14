package com.wallapop.iam.keycloak

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.http.ContentType
import io.ktor.http.parameters
import io.ktor.serialization.jackson.JacksonConverter

class KeycloakClient(
    private val host: String,
    private val realm: String,
    private val clientId: String,
    private val clientSecret: String,
) {
    private val client =
        HttpClient(CIO) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = clientId, password = clientSecret)
                    }
                }
            }
            install(ContentNegotiation) {
                register(
                    ContentType.Application.Json,
                    JacksonConverter(
                        jacksonObjectMapper()
                            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE),
                    ),
                )
            }
        }

    suspend fun exchangeCodeForToken(
        code: String,
        codeVerifier: String,
        redirectUri: String,
    ): TokenResponse =
        client.submitForm(
            url = "$host/realms/$realm/protocol/openid-connect/token",
            formParameters =
                parameters {
                    append("code", code)
                    append("code_verifier", codeVerifier)
                    append("grant_type", "authorization_code")
                    append("redirect_uri", redirectUri)
                },
        ).body()

    fun close() {
        client.close()
    }

    data class TokenResponse(
        val accessToken: String,
        val expiresIn: Int,
        val refreshExpiresIn: Int,
        val refreshToken: String,
        val tokenType: String,
        val sessionState: String,
        val scope: String,
    )
}
