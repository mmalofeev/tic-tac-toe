package org.jub.kotlin.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.websocket.*
import org.jub.kotlin.frontend.utils.grid
import org.jub.kotlin.frontend.utils.startScreenButton
import org.jub.kotlin.frontend.utils.title

@Composable
fun mainScreen(viewModel: ViewModel, webSocket: DefaultWebSocketSession) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        title(viewModel)
        grid(viewModel, webSocket)
        startScreenButton(viewModel)
    }
}
