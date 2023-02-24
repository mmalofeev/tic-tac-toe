package org.jub.kotlin.utils

import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.jub.kotlin.models.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.math.abs

fun getHashOfTheGame(numberOfGames: Int): Long =
    abs(((Random.nextLong() + numberOfGames.toLong()) * 733_241) % 877_567 * 636_919 % 1_000_000_007)

fun checkWin(field: List<Signs>) =
    checkHorizontalWin(field) ?: checkVerticalWin(field) ?: checkDiagonalWin(field) ?: Signs.EMPTY

private fun checkVerticalWin(field: List<Signs>): Signs? {
    for (i in 0 until 3) {
        if (field[i] == field[3 + i] && field[i] == field[6 + i]) {
            println("WON ${field[i]}")
            return field[i]
        }
    }
    return null
}

private fun checkHorizontalWin(field: List<Signs>): Signs? {
    for (i in 0 until 3) {
        if (field[3 * i] == field[3 * i + 1] && field[3 * i] == field[3 * i + 2]) {
            println("WON ${field[3 * i]}")
            return field[3 * i]
        }
    }
    return null
}

private fun checkDiagonalWin(field: List<Signs>): Signs? {
    if (field[0] == field[4] && field[0] == field[8]) {
        println("WON ${field[0]}")
        return field[0]
    }
    if (field[2] == field[4] && field[2] == field[6]) {
        println("WON ${field[0]}")
        return field[2]
    }
    return null
}

suspend fun sendResponse(
    popUpMessage: String?,
    field: List<Signs>?,
    turnMessage: String,
    webSocket: DefaultWebSocketSession
) {
    val response = ServerResponse(popUpMessage, field, turnMessage)
    val jsonResponse = Json.encodeToJsonElement(response)
    val jsonFrame = Frame.Text(jsonResponse.toString())
    webSocket.send(jsonFrame)
    println("Send $jsonResponse")
}

suspend fun handleJoinGame(json: String, player: Player) {
    println("starting joining game")
    val action = Json.decodeFromString<JoinGameAction>(json)
    val game = games[action.gameId]
    game ?: run {
        sendResponse("Wrong game id!", null, "", player.session)
        return
    }
    if (action.gameMode == GameMode.SINGLE) {
        game.mode = GameMode.SINGLE
        game.whoseTurn = 0
        game.firstTurn = 0
    }
    if (game.players.size == 2 || (game.players.size == 1 && game.mode == GameMode.SINGLE)) {
        sendResponse("Lobby is full!", null, "", player.session)
        return
    }
    if (game.players.size == 0) {
        game.players.add(player)
        val turnMessage = if (game.firstTurn == 0) "Your turn" else "Your opponent's turn"
        sendResponse(null, EMPTY_FIELD, turnMessage, player.session)
        println("First player joined")
    } else {
        game.players.add(player)
        val turnMessage = if (game.firstTurn == 1) "Your turn" else "Your opponent turn"
        sendResponse(null, EMPTY_FIELD, turnMessage, player.session)
        println("Second player joined")
    }
}

suspend fun hasTurnErrors(game: Game, player: Player, action: DoTurnAction): Boolean =
    if (game.mode == GameMode.MULTIPLAYER && game.players.size == 1) {
        val turnMessage = if (game.firstTurn == 0) "Your turn" else "Your opponent's turn"
        sendResponse("Waiting for second player!", game.field, turnMessage, player.session)
        true
    } else if (game.status == GameStatus.ENDED) {
        sendResponse("Game has already ended!", game.field, "Game over", player.session)
        true
    } else if (player != game.players[game.whoseTurn]) {
        sendResponse(
            "It's opponent's turn!",
            game.field,
            "Your opponent's turn",
            player.session
        )
        true
    } else if (game.field[action.cellNumber] != Signs.EMPTY) {
        sendResponse("You must choose an empty cell!", game.field, "Your turn", player.session)
        true
    } else {
        false
    }

suspend fun processTurnSinglePlayer(player: Player, game: Game) {
    val field = game.field.toMutableList()
    val crossWinResult = checkWin(field)
    if (crossWinResult == Signs.CROSS) {
        sendResponse("You won", game.field, "Game over", player.session)
        game.status = GameStatus.ENDED
        return
    }
    if (field.none { it == Signs.EMPTY }) {
        println("Draw")
        sendResponse("Draw", game.field, "Game has ended", player.session)
        game.status = GameStatus.ENDED
        return
    }
    sendResponse(null, game.field, "Your opponent's turn", player.session)
    withContext(Dispatchers.IO) {
        TimeUnit.MILLISECONDS.sleep(500)
        val emptyCellNumber = (0..8).filter { game.field[it] == Signs.EMPTY }.random()
        field[emptyCellNumber] = Signs.NOUGHT
        game.field = field
        val noughtWinResult = checkWin(field)
        if (noughtWinResult == Signs.NOUGHT) {
            sendResponse("You lost", game.field, "Game over", player.session)
            game.status = GameStatus.ENDED
        } else {
            sendResponse(null, game.field, "Your turn", player.session)
        }
    }
}

suspend fun processTurnMultiPlayer(game: Game) {
    when (checkWin(game.field)) {
        Signs.CROSS -> {
            processCrossWin(game)
        }
        Signs.NOUGHT -> {
            processNoughtWin(game)
        }
        Signs.EMPTY -> {
            if (isDraw(game)) return
            for (gamePlayer in game.players) {
                val turnMessage =
                    if (gamePlayer == game.players[game.whoseTurn]) "Your opponent's turn" else "Your turn"
                sendResponse(null, game.field, turnMessage, gamePlayer.session)
            }
            game.whoseTurn += 1
            game.whoseTurn %= 2
        }
    }
}

suspend fun handleDoTurnAction(json: String, player: Player) {
    val action = Json.decodeFromString<DoTurnAction>(json)
    val game = games[action.gameId]!!
    if (hasTurnErrors(game, player, action)) {
        println("Error occured")
        return
    }
    val field = game.field.toMutableList()
    if (game.whoseTurn == game.firstTurn) {
        field[action.cellNumber] = Signs.CROSS
    } else {
        field[action.cellNumber] = Signs.NOUGHT
    }
    game.field = field
    when (game.mode) {
        GameMode.SINGLE -> {
            processTurnSinglePlayer(player, game)
        }
        GameMode.MULTIPLAYER -> {
            processTurnMultiPlayer(game)
        }
    }
}

suspend fun isDraw(game: Game): Boolean {
    if (game.field.none { it == Signs.EMPTY }) {
        for (gamePlayer in game.players) {
            sendResponse("Draw", game.field, "Game has ended", gamePlayer.session)
            game.status = GameStatus.ENDED
        }
        return true
    }
    return false
}

suspend fun processCrossWin(game: Game) {
    for (gamePlayer in game.players) {
        if (gamePlayer == game.players[game.firstTurn]) {
            sendResponse("You won", game.field, "Game over", gamePlayer.session)
            game.status = GameStatus.ENDED
        } else {
            sendResponse("You lost", game.field, "Game over", gamePlayer.session)
            game.status = GameStatus.ENDED
        }
    }
}

suspend fun processNoughtWin(game: Game) {
    for (gamePlayer in game.players) {
        if (gamePlayer == game.players[game.firstTurn]) {
            sendResponse("You lost", game.field, "Game over", gamePlayer.session)
            game.status = GameStatus.ENDED
        } else {
            sendResponse("You won", game.field, "Game over", gamePlayer.session)
            game.status = GameStatus.ENDED
        }
    }
}
