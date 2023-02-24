package org.jub.kotlin.client

import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.jub.kotlin.frontend.ViewModel
import org.jub.kotlin.models.*

suspend fun DefaultClientWebSocketSession.getServerResponse(viewModel: ViewModel) {
    for (message in incoming) {
        println(message)
        message as? Frame.Text ?: continue
        val response = Json.decodeFromString<ServerResponse>(message.readText())
        viewModel.popUpMessage = response.popUpMessage
        viewModel.turnMessage = response.turnMessage ?: viewModel.turnMessage
        viewModel.field = response.field
        if (viewModel.screen == Screen.START_SCREEN) {
            response.popUpMessage ?: run {
                val newScreen = Screen.MAIN_SCREEN
                viewModel.screen = newScreen
            }
        }
    }
}

fun DefaultWebSocketSession.sendYourTurn(gameId: Long, cellNumber: Int) {
    val action = DoTurnAction(gameId, cellNumber)
    val json = Json.encodeToJsonElement(action)
    val turnActionFrame = Frame.Text("turn=$json")
    println("send $turnActionFrame")
    runBlocking {
        send(turnActionFrame)
    }
}
