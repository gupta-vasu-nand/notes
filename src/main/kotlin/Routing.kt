package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.repository.Repo
import com.example.routes.noteRoutes
import com.example.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val db = Repo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }
    routing {
        get ("/"){
            call.respondText { "Hello World" }
        }
        userRoutes(db, jwtService, hashFunction)
        noteRoutes(db, hashFunction)
    }
}
