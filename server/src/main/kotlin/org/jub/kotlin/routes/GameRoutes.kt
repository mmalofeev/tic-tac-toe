package org.jub.kotlin.routes

import org.jub.kotlin.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jub.kotlin.models.Game
import org.jub.kotlin.models.GameStatus
import org.jub.kotlin.models.games
import org.jub.kotlin.models.numberOfGames
import kotlin.random.Random

fun Route.gameRouting() {
    route("/create_game") {
        get {
            numberOfGames++
            val gameHash = getHashOfTheGame(numberOfGames)
            println("ROutiong: gamehash is $gameHash")
            val whoseTurn = Random.nextInt(0, 2)
            val newGame = Game(gameHash, whoseTurn, GameStatus.WAITING)
            games[gameHash] = newGame
            call.respondText("$gameHash", status = HttpStatusCode.OK)
        }
    }
}

