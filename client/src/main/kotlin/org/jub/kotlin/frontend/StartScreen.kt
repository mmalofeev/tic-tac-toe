package org.jub.kotlin.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.*
import io.ktor.websocket.*
import org.jub.kotlin.frontend.utils.*

const val GEN_LOBBY_ID = "Generated lobby id is "
var gameIdMessage by mutableStateOf<String>(GEN_LOBBY_ID + "0")

@Composable
fun startScreen(viewModel: ViewModel, client: HttpClient, webSocket: DefaultWebSocketSession) {
    var joinGameId by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Tic Tac Toe",
            fontSize = 70.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Magenta
        )
        createNewLobbyButton(client)
        OutlinedTextField(
            value = joinGameId,
            onValueChange = { joinGameId = it },
            label = { Text("Insert game id to join the lobby") }
        )
        gameIdMessage()
        joinSinglePlayerLobbyButton(viewModel, joinGameId, webSocket)
        joinMultiplayerLobbyButton(viewModel, joinGameId, webSocket)
    }
}
