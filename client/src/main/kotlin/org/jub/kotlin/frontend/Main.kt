package org.jub.kotlin.frontend

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Job
import org.jub.kotlin.client.getServerResponse
import org.jub.kotlin.models.Screen

fun main() {
    val viewModel = ViewModel(Screen.START_SCREEN)
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        var serverResponse: Job? = null
        client.webSocket(method = HttpMethod.Get, host = "0.0.0.0", port = 8080, path = "/tic-tac-toe") {
            application {
                serverResponse = launch { this@webSocket.getServerResponse(viewModel) }
                Window(
                    onCloseRequest = {
                        exitApplication()
                        client.close()
                    },
                    title = "Tic-tac-toe",
                    state = rememberWindowState(width = 600.dp, height = 1000.dp)
                ) {
                    if (viewModel.screen == Screen.START_SCREEN) {
                        startScreen(viewModel, client, this@webSocket)
                    } else {
                        mainScreen(viewModel, this@webSocket)
                    }
                    viewModel.popUpMessage?.let {
                        showPopUpMessage(viewModel)
                    }
                }
            }
        }
        serverResponse?.join()
    }
}
