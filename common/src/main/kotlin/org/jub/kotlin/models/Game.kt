package org.jub.kotlin.models

import io.ktor.websocket.*
import kotlinx.serialization.Serializable

@Serializable
enum class GameStatus {
    ENDED,
    WAITING,
    ;
}

@Serializable
enum class Signs {
    CROSS,
    EMPTY,
    NOUGHT,
    ;
}

@Serializable
enum class GameMode {
    MULTIPLAYER,
    SINGLE,
    ;
}

const val JOIN_GAME_TYPE = "join"

const val DO_TURN_TYPE = "turn"

const val YOUR_TURN_MESSAGE = "Your turn"

const val OPPONENT_TURN_MESSAGE = "Your opponent's turn"

val EMPTY_FIELD = listOf(
    Signs.EMPTY,
    Signs.EMPTY,
    Signs.EMPTY,
    Signs.EMPTY,
    Signs.EMPTY,
    Signs.EMPTY,
    Signs.EMPTY,
    Signs.EMPTY,
    Signs.EMPTY
)

@Serializable
data class Player(val session: DefaultWebSocketSession)

@Serializable
data class Game(
    var id: Long,
    var whoseTurn: Int,
    var status: GameStatus,
    var mode: GameMode = GameMode.MULTIPLAYER,
    var firstTurn: Int = whoseTurn,
    var field: List<Signs> = EMPTY_FIELD,
    val players: MutableList<Player> = mutableListOf(),
)

val games: MutableMap<Long, Game> = HashMap()

@Serializable
data class JoinGameAction(val gameId: Long, val gameMode: GameMode = GameMode.MULTIPLAYER)

@Serializable
data class ServerResponse(val popUpMessage: String?, var field: List<Signs>?, var turnMessage: String?)

@Serializable
data class DoTurnAction(val gameId: Long, val cellNumber: Int, val gameMode: GameMode = GameMode.MULTIPLAYER)

enum class Screen {
    MAIN_SCREEN,
    START_SCREEN,
    ;
}

var numberOfGames = 0
