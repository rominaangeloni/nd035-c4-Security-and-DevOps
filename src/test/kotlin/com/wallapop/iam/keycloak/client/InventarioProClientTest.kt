package com.wallapop.iam.keycloak.client

import com.wallapop.iam.keycloak.oauth2.flow.AuthorizationCodeFlow
import com.wallapop.iam.keycloak.oauth2.flow.RefreshTokenFlow

private const val CLIENT_ID = "inventario-pro"
private const val CLIENT_SECRET = "Q2CTUVOevUpl5t5t6NghzZftkGIgUOZE"

class InventarioProClientACFTest : AuthorizationCodeFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)

class InventarioProClientRTFTest : RefreshTokenFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)
