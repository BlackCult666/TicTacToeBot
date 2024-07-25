package callback

import game.lobby.Lobbies
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import utils.editMessage
import utils.getLobbyKeyboard
import utils.mentionPlayer

class StartCallback(
    private val bot: Bot,
    private val lobbies: Lobbies
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val query = callbackQuery.data.get()
        if (!query.startsWith("start")) return

        val action = query.split("_")[1]
        when (action) {
            "play" -> handlePlayCallback(callbackQuery)
            "settings" -> handleSettings(callbackQuery)
        }
    }

    private fun handlePlayCallback(callbackQuery: CallbackQuery) {
        val sender = callbackQuery.sender
        val mention = mentionPlayer(sender.id, sender.firstName)
        val lobby = lobbies.createLobby(callbackQuery.id, sender)

        println(mention)

        val textMessage = """
            $mention wants to start a new match.
            
            Press the button below to join!
        """.trimIndent()

        bot.editMessage(callbackQuery, textMessage, getLobbyKeyboard(lobby.id))
    }

    private fun handleSettings(callbackQuery: CallbackQuery) {
        bot.editMessage(callbackQuery, "Soon")
    }
}