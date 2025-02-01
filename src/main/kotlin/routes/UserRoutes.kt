package com.example.routes

import com.example.authentication.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"


fun Route.userRoutes(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String)->String
) {
    post(REGISTER_REQUEST) {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        }catch (e:Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Missing Some Fields"))
            return@post
        }
        try {
            val user = User(email = registerRequest.email, userName = registerRequest.name, hashPassword = hashFunction(registerRequest.password))
            db.addUser(user)
            call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
        }catch (e: Exception){
            call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message?:"Some problem occurred"))
        }
    }

    post(LOGIN_REQUEST) {
        val loginRequest = try {
            call.receive<LoginRequest>()
        }catch (e:Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Missing Some Fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Wrong email Id"))
            }else{
                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, jwtService.generateToken(user)))
                }else{
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Wrong password"))
                }
            }
        }catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, e.message?:"Some problem occurred"))
        }
    }
}