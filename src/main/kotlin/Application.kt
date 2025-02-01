package com.example

import com.example.repository.DatabaseFactory
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    DatabaseFactory.init()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
