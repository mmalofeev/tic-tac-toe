package org.jub.kotlin.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.websocket.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import org.jub.kotlin.client.sendYourTurn
import org.jub.kotlin.models.*

class ViewModel(screen: Screen) {
    var field: List<Signs>? by mutableStateOf(null)
    var screen by mutableStateOf(screen)
    var turnMessage by mutableStateOf("")
    var popUpMessage by mutableStateOf<String?>(null)
    var gameId by mutableStateOf<Long>(0)

    fun processClick(cellNumber: Int, webSocket: DefaultWebSocketSession) {
        println("PROCESS CLICK")
        webSocket.sendYourTurn(gameId, cellNumber)
        println("HERE")
    }
}

@Composable
fun showPopUpMessage(viewModel: ViewModel) {
    Window(
        onCloseRequest = {
            viewModel.popUpMessage = null
        },
        visible = true,
        title = "Pop Up Message",
        state = rememberWindowState(width = 300.dp, height = 200.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = viewModel.popUpMessage!!,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal
            )
        }
    }
}
