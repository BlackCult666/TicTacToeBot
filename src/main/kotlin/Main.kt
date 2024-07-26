import callback.*
import database.MongoWrapper
import game.Matches
import game.lobby.Lobbies
import inline.StartInlineQuery
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot

class TicTacToeBot(
    bot: Bot
) : LongPollingBot(bot) {

    init {
        val mongoWrapper = MongoWrapper()
        val lobbies = Lobbies()
        val matches = Matches()

        events.apply {
            registerUpdateHandler(BackCallback(bot, mongoWrapper))
            registerUpdateHandler(LangCallback(bot, mongoWrapper))
            registerUpdateHandler(StartInlineQuery(bot, mongoWrapper))
            registerUpdateHandler(MatchCallback(bot, matches, mongoWrapper))
            registerUpdateHandler(StartCallback(bot, lobbies, mongoWrapper))
            registerUpdateHandler(LobbyCallback(bot, lobbies, matches, mongoWrapper))
        }
    }
}

fun main() {
    val token = "5487274600:AAGMjOQoU4hVO_88UBUjjKfOADVJ00GrXgQ"
    val bot = Bot.fromToken(token)

    TicTacToeBot(bot).run()
}