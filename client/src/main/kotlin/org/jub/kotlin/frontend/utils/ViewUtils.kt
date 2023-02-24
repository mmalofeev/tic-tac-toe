package org.jub.kotlin.frontend.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jub.kotlin.frontend.*
import org.jub.kotlin.models.GameMode
import org.jub.kotlin.models.JoinGameAction
import org.jub.kotlin.models.Screen
import org.jub.kotlin.models.Signs

@Composable
fun createButton(buttonText: String, onClickAction: () -> Unit) {
    Button(
        onClick = onClickAction,
        shape = RoundedCornerShape(5.dp),
        elevation = ButtonDefaults.elevation(5.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(
            text = buttonText,
            fontSize = 16.sp
        )
    }
}

@Composable
fun createCentralButton(buttonText: String, onClickAction: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        createButton(buttonText, onClickAction)
    }
}

@Composable
fun createNewLobbyButton(client: HttpClient) {
    createCentralButton(
        "Create new lobby"
    ) {
        runBlocking {
            val response = client.get("http://0.0.0.0:8080/create_game")
            val gameHash = response.bodyAsText()
            println("GameHash is $gameHash")
            gameIdMessage = GEN_LOBBY_ID + gameHash
        }
    }
}

@Composable
fun joinSinglePlayerLobbyButton(viewModel: ViewModel, joinGameId: String, webSocket: DefaultWebSocketSession) {
    createCentralButton(
        "Join single player lobby",
    ) {
        viewModel.gameId = joinGameId.trim().toLong()
        val joinGameAction = JoinGameAction(viewModel.gameId, GameMode.SINGLE)
        val jsonAction = Json.encodeToString(joinGameAction)
        runBlocking {
            println("Send $jsonAction")
            webSocket.send(Frame.Text("join=$jsonAction"))
        }
    }
}

@Composable
fun joinMultiplayerLobbyButton(viewModel: ViewModel, joinGameId: String, webSocket: DefaultWebSocketSession) {
    createCentralButton(
        "Join multiplayer lobby"
    ) {
        viewModel.gameId = joinGameId.trim().toLong()
        val joinGameAction = JoinGameAction(joinGameId.trim().toLong())
        val jsonAction = Json.encodeToString(joinGameAction)
        runBlocking {
            println("sending $jsonAction")
            webSocket.send(Frame.Text("join=$jsonAction"))
        }
    }
}

@Composable
fun title(viewModel: ViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Lobby id: ${viewModel.gameId}", fontSize = 16.sp, textAlign = TextAlign.Center)
    }
    Text(
        text = "Tic Tac Toe",
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Magenta
    )
}

@Composable
fun grid(viewModel: ViewModel, webSocket: DefaultWebSocketSession) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        ticTacToeGrid()
        LazyVerticalGrid(
            modifier = Modifier
                .aspectRatio(1f),
            columns = GridCells.Fixed(3)
        ) {
            for (cellNumber in 0 until 9) {
                item {
                    gridCell(viewModel, cellNumber, webSocket)
                }
            }
        }
    }
}

@Composable
fun gridCell(viewModel: ViewModel, cellNumber: Int, webSocket: DefaultWebSocketSession) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { viewModel.processClick(cellNumber, webSocket) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = true,
        ) {
            if (viewModel.field!![cellNumber] == Signs.NOUGHT) {
                nought()
            } else if (viewModel.field!![cellNumber] == Signs.CROSS) {
                cross()
            }
        }
    }
}

@Composable
fun startScreenButton(viewModel: ViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = viewModel.turnMessage,
            fontSize = 24.sp,
            fontStyle = FontStyle.Italic
        )
        createButton(
            "Start Screen",
            ) {
            val newScreen = Screen.START_SCREEN
            viewModel.screen = newScreen
        }
    }
}

@Composable
fun gameIdMessage() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = gameIdMessage,
            fontSize = 16.sp
        )
    }
}
