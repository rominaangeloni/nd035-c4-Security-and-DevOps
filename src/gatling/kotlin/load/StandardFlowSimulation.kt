package load

import com.typesafe.config.ConfigFactory
import io.gatling.javaapi.core.CoreDsl.atOnceUsers
import io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.css
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.CoreDsl.nothingFor
import io.gatling.javaapi.core.CoreDsl.rampUsers
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.CoreDsl.stressPeakUsers
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.header
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import io.gatling.javaapi.http.HttpProtocolBuilder
import java.time.Duration
import java.util.UUID
import kotlin.random.Random

@Suppress("unused")
class StandardFlowSimulation : Simulation() {
    val config = StandardFlowSimulationConfig()
    val concurrentUserCount = config.concurrentUsers
    val simulationDuration = config.duration
    val keycloakUrl = config.keycloakUrl
    val realm = config.keycloakRealm
    val keycloakUserCount = config.keycloakUsers
    val keycloakClients = config.keycloakClients
    val keycloakClientCount = keycloakClients.size
    val userAgent = config.userAgent
    val httpProtocol: HttpProtocolBuilder = http.baseUrl(keycloakUrl).disableFollowRedirect()

    val feeder = generateSequence {
        val randomUserId = Random.nextInt(keycloakUserCount)
        val randomClientId = Random.nextInt(keycloakClientCount)
        val pkceVerifier = pkceVerifier()
        mapOf(
            "userName" to "${config.keycloakUsersPrefix}$randomUserId",
            "clientId" to "${config.keycloakClientsPrefix}$randomClientId",
            "clientSecret" to keycloakClients[randomClientId],
            "redirectUrl" to "http://client-app-$randomClientId/sso/login",
            "logoutUrl" to "http://client-app-$randomClientId/logout",
            "pkceVerifier" to pkceVerifier,
            "pkceCodeChallenge" to pkceCodeChallenge(pkceVerifier),
        )
    }

    val loadLoginPage = exec(
        http("keycloak_get_login-page").get("/realms/$realm/protocol/openid-connect/auth")
            .header("User-Agent", userAgent)
            .queryParam("client_id", "#{clientId}")
            .queryParam("redirect_uri", "#{redirectUrl}")
            .queryParam("state", UUID.randomUUID().toString())
            .queryParam("nonce", UUID.randomUUID().toString())
            .queryParam("response_type", "code")
            .queryParam("scope", "openid")
            .queryParam("code_challenge", "#{pkceCodeChallenge}")
            .queryParam("code_challenge_method", "S256")
            .check(status().`is`(200))
            .check(css("#kc-form-login", "action").saveAs("auth_url")),
    )

    val authenticate = exec(
        http("keycloak_post_authentication")
            .post("#{auth_url}")
            .header("User-Agent", userAgent)
            .formParam("username", "#{userName}")
            .formParam("password", "user")
            .check(status().`is`(302))
            .check(
                header("Location").transform {
                    // TODO: code is always last parameter?
                    it.substring(it.indexOf("code=") + 5, it.length)
                }.saveAs("code"),
            ),
    )
    val codeToToken = exec(
        http("client-application_post_code-to-token")
            .post("/realms/$realm/protocol/openid-connect/token")
            .header("User-Agent", userAgent)
            .formParam("grant_type", "authorization_code")
            .formParam("code", "#{code}")
            .formParam("code_verifier", "#{pkceVerifier}")
            .formParam("client_id", "#{clientId}")
            .formParam("client_secret", "#{clientSecret}")
            .formParam("redirect_uri", "#{redirectUrl}")
            .check(status().`is`(200))
            .check(jsonPath("$..access_token").exists())
            .check(jsonPath("$..refresh_token").exists().saveAs("refresh_token")),
    )

    val refreshToken = exec(
        http("client-application_refresh-token")
            .post("/realms/$realm/protocol/openid-connect/token")
            .header("User-Agent", userAgent)
            .formParam("grant_type", "refresh_token")
            .formParam("refresh_token", "#{refresh_token}")
            .formParam("auth_method", "client_secret_basic")
            .formParam("client_id", "#{clientId}")
            .formParam("client_secret", "#{clientSecret}")
            .check(status().`is`(200))
            .check(jsonPath("$..access_token").exists()),
    )

    /**
     * https://auscunningham.medium.com/keycloak-19-0-1-and-setting-the-id-token-hint-afd29fd98c76
     * https://stackoverflow.com/questions/74327614/keycloak-18-0-2-get-id-token-hint-for-logout-url-by-the-api-call
     */
    val logout = exec(
        http("client-application_get_logout")
//            .get("/realms/$realm/protocol/openid-connect/logout?post_logout_redirect_uri=#{logoutUrl}")
            .get("/realms/$realm/protocol/openid-connect/logout")
            .header("User-Agent", userAgent)
            .check(status().`is`(200)),
//            .check(status().`is`(302))
//            .check(header("Location").`is`("#{logoutUrl}"))
    )

    val keycloakStandardFlow = scenario("keycloak-standard-flow")
        .feed(feeder.iterator())
        .exec(loadLoginPage)
        .pause(5)
        .group("authentication-round-trip").on({
            exec(authenticate)
                .pause(1)
                .exec(codeToToken)
                .pause(1)
                .exec(refreshToken)
        })
        .pause(10).exec(logout)

    init {
        // https://docs.gatling.io/reference/script/core/injection/
        setUp(workload())
            .protocols(httpProtocol)
    }

    private fun workload() = if (config.openWorkload) openWorkload() else closedWorkload()

    private fun closedWorkload() = keycloakStandardFlow.injectClosed(
        constantConcurrentUsers(concurrentUserCount).during(simulationDuration),
    )

    private fun openWorkload() = keycloakStandardFlow.injectOpen(
        nothingFor(4),
        atOnceUsers(10),
        rampUsers(10).during(5),
        constantUsersPerSec(20.0).during(15),
        constantUsersPerSec(20.0).during(15).randomized(),
        rampUsersPerSec(10.0).to(20.0).during(10),
        rampUsersPerSec(10.0).to(20.0).during(10).randomized(),
        stressPeakUsers(concurrentUserCount).during(120),
    )

    data class StandardFlowSimulationConfig(
        val openWorkload: Boolean,
        val concurrentUsers: Int,
        val duration: Duration,
        val keycloakUrl: String,
        val keycloakRealm: String,
        val keycloakUsers: Int,
        val keycloakUsersPrefix: String,
        val keycloakClients: List<String>,
        val keycloakClientsPrefix: String,
        val userAgent: String,
    ) {
        companion object {
            operator fun invoke() = with(ConfigFactory.load().getConfig("standard-flow-simulation")) {
                StandardFlowSimulationConfig(
                    openWorkload = getBoolean("open-workload"),
                    concurrentUsers = getInt("concurrent-users"),
                    duration = getDuration("duration"),
                    keycloakUrl = getString("keycloak.url"),
                    keycloakRealm = getString("keycloak.realm"),
                    keycloakUsers = getInt("keycloak.users"),
                    keycloakUsersPrefix = getString("keycloak.users-prefix"),
                    keycloakClients = getStringList("keycloak.clients"),
                    keycloakClientsPrefix = getString("keycloak.clients-prefix"),
                    userAgent = getString("user-agent"),
                )
            }
        }
    }
}
