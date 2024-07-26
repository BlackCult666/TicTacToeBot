import callback.*
import database.Database
import game.Matches
import game.lobby.Lobbies
import inline.StartInlineQuery
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot

class TicTacToeBot(
    bot: Bot
) : LongPollingBot(bot) {

    init {
        val database = Database()
        val lobbies = Lobbies()
        val matches = Matches()

        events.apply {
            registerUpdateHandler(BackCallback(bot, database))
            registerUpdateHandler(LangCallback(bot, database))
            registerUpdateHandler(StartInlineQuery(bot, database))
            registerUpdateHandler(MatchCallback(bot, matches, database))
            registerUpdateHandler(StartCallback(bot, lobbies, database))
            registerUpdateHandler(LobbyCallback(bot, lobbies, matches, database))
        }
    }
}

fun main() {
    val token = "5487274600:AAGMjOQoU4hVO_88UBUjjKfOADVJ00GrXgQ"
    val bot = Bot.fromToken(token)

    TicTacToeBot(bot).run()
}