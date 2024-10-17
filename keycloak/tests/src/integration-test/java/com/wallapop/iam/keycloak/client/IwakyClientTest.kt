package com.wallapop.iam.keycloak.client

import com.wallapop.iam.keycloak.oauth2.flow.AuthorizationCodeFlow
import com.wallapop.iam.keycloak.oauth2.flow.RefreshTokenFlow

private const val CLIENT_ID = "iwaky"
private const val CLIENT_SECRET = "7RjkQu89SOXC4xkhZNzPQVBkyk1zDWfP"

class IwakyClientACFTest : AuthorizationCodeFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)

class IwakyProClientRTFTest : RefreshTokenFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)
