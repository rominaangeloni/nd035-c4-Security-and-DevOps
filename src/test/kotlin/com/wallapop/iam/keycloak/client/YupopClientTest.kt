package com.wallapop.iam.keycloak.client

import com.wallapop.iam.keycloak.oauth2.flow.AuthorizationCodeFlow
import com.wallapop.iam.keycloak.oauth2.flow.RefreshTokenFlow

private const val CLIENT_ID = "yupop"
private const val CLIENT_SECRET = "3TrCXF5RzRAaesTimwQ9RLN8kOVTechR"

class YupopClientACFTest : AuthorizationCodeFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)

class YupopClientRTFTest : RefreshTokenFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)
