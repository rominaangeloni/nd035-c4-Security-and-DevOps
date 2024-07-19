package com.wallapop.iam.keycloak.client

import com.wallapop.iam.keycloak.oauth2.flow.AuthorizationCodeFlow
import com.wallapop.iam.keycloak.oauth2.flow.RefreshTokenFlow

private const val CLIENT_ID = "geek-market"
private const val CLIENT_SECRET = "KFVIMtIomC2vgIyUGb4ghhBgpHSRFcbD"

class GeekMarketClientACFTest : AuthorizationCodeFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)

class GeekMarketClientRTFTest : RefreshTokenFlow(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET)
