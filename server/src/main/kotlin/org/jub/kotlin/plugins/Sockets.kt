package org.jub.kotlin.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.jub.kotlin.utils.*
import org.jub.kotlin.models.*
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/tic-tac-toe") {
            val player = Player(this)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                val type = receivedText.substringBefore("=")
                val json = receivedText.substringAfter("=")
                println("Приняли текст $json с типом $type")
                when (type) {
                    JOIN_GAME_TYPE -> {
                        handleJoinGame(json, player)
                    }
                    DO_TURN_TYPE -> {
                        handleDoTurnAction(json, player)
                    }
                }
            }
        }
    }
}
