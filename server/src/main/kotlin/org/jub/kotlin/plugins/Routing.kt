package org.jub.kotlin.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jub.kotlin.routes.gameRouting

fun Application.configureRouting() {
    routing {
        gameRouting()
    }
}
