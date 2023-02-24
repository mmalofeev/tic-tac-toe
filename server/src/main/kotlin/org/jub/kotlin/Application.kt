package org.jub.kotlin

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jub.kotlin.plugins.configureRouting

import org.jub.kotlin.plugins.configureSockets

fun Application.module() {
    configureRouting()
    configureSockets()
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSockets()
    }.start(wait = true)
}
