package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.repository.Repo
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

fun Application.configureSecurity() {
    val db = Repo()
    // Please read the jwt property from the config file if you are using EngineMain
    /*
    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = "ktor sample app"
    val jwtSecret = "secret"
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
     */

    install(Authentication) {

        jwt ("jwt"){

            verifier(
                 JWT
                .require(Algorithm.HMAC512(System.getenv("JWT_SECRET") ?: "JaiShreeRam"))
                .withIssuer("noteServer")
                .build())
            realm = "Note Server"
            validate { credential ->
                val payload = credential.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }
}

@Serializable
data class MySession(val count: Int = 0)
