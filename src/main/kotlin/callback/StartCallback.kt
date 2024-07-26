package callback

import database.MongoWrapper
import game.lobby.Lobbies
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import locale.Languages
import utils.editMessage
import utils.getLangKeyboard
import utils.getLobbyKeyboard
import utils.mentionPlayer

class StartCallback(
    private val bot: Bot,
    private val lobbies: Lobbies,
    private val mongoWrapper: MongoWrapper
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val query = callbackQuery.data.get()
        if (!query.startsWith("start")) return

        val lang = mongoWrapper.getUserLang(callbackQuery.sender.id)
        val action = query.split("_")[1]
        when (action) {
            "play" -> handlePlayCallback(callbackQuery, lang)
            "languages" -> handleLanguages(callbackQuery, lang)
        }
    }

    private fun handlePlayCallback(callbackQuery: CallbackQuery, lang: String) {
        val sender = callbackQuery.sender
        val mention = mentionPlayer(sender.id, sender.firstName)
        val lobby = lobbies.createLobby(callbackQuery.id, sender)

        val textMessage = Languages.getMessage(lang, "lobby_start")
            .replace("{host}", mention)

        bot.editMessage(callbackQuery, textMessage, getLobbyKeyboard(lobby.id, lang))
    }

    private fun handleLanguages(callbackQuery: CallbackQuery, lang: String) {
        bot.editMessage(callbackQuery, Languages.getMessage(lang, "lang_text"), getLangKeyboard())
    }
}