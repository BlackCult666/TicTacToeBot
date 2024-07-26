package callback

import database.Database
import game.lobby.Lobbies
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import locale.Languages
import utils.*

class StartCallback(
    private val bot: Bot,
    private val lobbies: Lobbies,
    private val database: Database
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val query = callbackQuery.data.get()
        if (!query.startsWith("start")) return

        val sender = callbackQuery.sender
        val lang = database.getUserLang(callbackQuery.sender.id)
        val action = query.split("_")[1]

        val savedName = database.getUserInfo(sender.id).firstName
        if (savedName != sender.firstName) database.updateFirstName(sender.id, sender.firstName)

        when (action) {
            "play" -> handlePlayCallback(callbackQuery, lang)
            "stats" -> handleStatsCallback(callbackQuery, lang)
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

    private fun handleStatsCallback(callbackQuery: CallbackQuery, lang: String) {
        val sender = callbackQuery.sender
        val mention = mentionPlayer(sender.id, sender.firstName)

        val userStats = database.getUserInfo(sender.id)

        val matchWon = userStats.matchWon
        val matchLost = userStats.matchLost
        val actualStreak = userStats.actualStreak
        val bestStreak = userStats.bestStreak

        val totalMatch = matchLost + matchWon

        val winRatio = percentage(matchWon, totalMatch)
        val loseRatio = percentage(matchLost, totalMatch)


        val text = Languages.getMessage(lang, "stats_message")
            .replace("{mention}", mention)
            .replace("{matchWon}", matchWon.toString())
            .replace("{matchLost}", matchLost.toString())
            .replace("{totalMatch}", totalMatch.toString())
            .replace("{winRatio}", winRatio)
            .replace("{loseRatio}", loseRatio)
            .replace("{actualStreak}", actualStreak.toString())
            .replace("{bestStreak}", bestStreak.toString())

        bot.editMessage(callbackQuery, text, InlineKeyboardMarkup(getBackButton(lang)))
    }

    private fun handleLanguages(callbackQuery: CallbackQuery, lang: String) {
        bot.editMessage(callbackQuery, Languages.getMessage(lang, "lang_text"), getLangKeyboard())
    }
}