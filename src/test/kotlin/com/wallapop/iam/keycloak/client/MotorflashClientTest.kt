package com.wallapop.iam.keycloak.client

import com.wallapop.iam.keycloak.oauth2.flow.AuthorizationCodeFlow
import com.wallapop.iam.keycloak.oauth2.flow.RefreshTokenFlow

private const val CLIENT_ID = "motorflash"
private const val CLIENT_SECRET = "c484fd26e33760742d94ce2f3ab028b1"

class MotorflashClientACFTest : AuthorizationCodeFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)

class MotorflashgClientRTFTest : RefreshTokenFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)
