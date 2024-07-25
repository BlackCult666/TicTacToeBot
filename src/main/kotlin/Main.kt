import callback.LobbyCallback
import callback.MatchCallback
import callback.StartCallback
import game.Matches
import game.lobby.Lobbies
import inline.StartInlineQuery
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot

class TicTacToeBot(
    bot: Bot
) : LongPollingBot(bot) {

    init {
        val lobbies = Lobbies()
        val matches = Matches()

        events.apply {
            registerUpdateHandler(StartInlineQuery(bot))
            registerUpdateHandler(StartCallback(bot, lobbies))
            registerUpdateHandler(MatchCallback(bot, matches))
            registerUpdateHandler(LobbyCallback(bot, lobbies, matches))
        }
    }
}

fun main() {
    val token = "5487274600:AAGMjOQoU4hVO_88UBUjjKfOADVJ00GrXgQ"
    val bot = Bot.fromToken(token)

    TicTacToeBot(bot).run()
}