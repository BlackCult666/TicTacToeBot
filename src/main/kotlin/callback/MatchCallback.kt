package callback

import database.Database
import game.Match
import game.MatchResult
import game.Matches
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.users.User
import locale.Languages
import utils.answerCallback
import utils.editMessage
import utils.mentionPlayer

class MatchCallback(
    private val bot: Bot,
    private val matches: Matches,
    private val database: Database
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("match")) return

        val sender = callbackQuery.sender
        val index = data.split("_")[2]

        val matchId = data.split("_")[1]
        val match = matches.getMatch(matchId)

        if (match == null) {
            bot.answerCallback(callbackQuery, Languages.getMessage("en", "expired_match"), true)
            return
        }

        val savedName = database.getUserInfo(sender.id).firstName
        if (savedName != sender.firstName) database.updateFirstName(sender.id, sender.firstName)

        val lang = database.getUserLang(match.host.id)

        handleMatch(callbackQuery, lang, sender, match, index.toInt())
    }

    private fun handleMatch(callbackQuery: CallbackQuery, lang: String, sender: User, match: Match, index: Int) {
        if (sender != match.currentPlayer) {
            bot.answerCallback(callbackQuery, Languages.getMessage(lang, "wrong_turn"), true)
            return
        }

        val result = match.makeMove(index)
        when (result) {
            MatchResult.IN_PROGRESS -> handleInProgress(callbackQuery, lang, match)

            MatchResult.INVALID_MOVE -> bot.answerCallback(callbackQuery, Languages.getMessage(lang, "spot_used"), true)

            MatchResult.X_WINS -> handleFinals(callbackQuery, lang, match.playerX, match.playerO, match, false)

            MatchResult.O_WINS -> handleFinals(callbackQuery, lang, match.playerO, match.playerX, match, false)

            MatchResult.DRAW -> handleFinals(callbackQuery, lang, match.playerX, match.playerO, match, true)
        }
    }

    private fun handleInProgress(callbackQuery: CallbackQuery, lang: String, match: Match) {
        val symbol = if (match.currentPlayer == match.playerX) "❌" else "⭕"
        val text = Languages.getMessage(lang, "match_progress")
            .replace("{currentPlayer}", mentionPlayer(match.currentPlayer.id, match.currentPlayer.firstName))
            .replace("{teamSymbol}", symbol)

        bot.editMessage(callbackQuery, text, match.getKeyboard(lang, false))
    }

    private fun handleFinals(callbackQuery: CallbackQuery, lang: String, winner: User, otherPlayer: User, match: Match, draw: Boolean) {
        val string = if (draw) "match_draw" else "match_won"
        val symbol = if (winner == match.playerX) "❌" else "⭕"

        if (!database.userExists(winner.id)) database.addUser(winner.id, winner.firstName)
        if (!database.userExists(otherPlayer.id)) database.addUser(otherPlayer.id, otherPlayer.firstName)

        database.updateStats(winner.id, true)
        database.updateStats(otherPlayer.id, false)

        val text = Languages.getMessage(lang, string)
            .replace("{firstPlayer}", mentionPlayer(winner.id, winner.firstName))
            .replace("{secondPlayer}", mentionPlayer(otherPlayer.id, otherPlayer.firstName))
            .replace("{winnerName}", winner.firstName)
            .replace("{winnerSymbol}", symbol)

        bot.editMessage(callbackQuery, text, match.getKeyboard(lang, true))
        matches.removeMatch(match.id)
    }


}