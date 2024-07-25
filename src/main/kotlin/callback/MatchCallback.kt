package callback

import game.Match
import game.MatchResult
import game.Matches
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.users.User
import utils.answerCallback
import utils.editMessage
import utils.mentionPlayer

class MatchCallback(
    private val bot: Bot,
    private val matches: Matches
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("match")) return

        val sender = callbackQuery.sender
        val index = data.split("_")[2]

        val matchId = data.split("_")[1]
        val match = matches.getMatch(matchId)

        if (match == null) {
            bot.answerCallback(callbackQuery, "Match expired!", true)
            return
        }

        handleMatch(callbackQuery, sender, match, index.toInt())
    }

    private fun handleMatch(callbackQuery: CallbackQuery, sender: User, match: Match, index: Int) {
        if (sender != match.currentPlayer) {
            bot.answerCallback(callbackQuery, "Not your turn!", true)
            return
        }

        val result = match.makeMove(index)
        when (result) {
            MatchResult.IN_PROGRESS -> {
                val text = """
                    Now it's the turn of ${mentionPlayer(match.currentPlayer.id, match.currentPlayer.firstName)}
                    
                    Pay attention to your choice!
                """.trimIndent()
                bot.editMessage(callbackQuery, text, match.getKeyboard(false))
            }

            MatchResult.INVALID_MOVE -> {
                bot.answerCallback(callbackQuery, "Spot already used!", true)
            }

            MatchResult.X_WINS -> {
                val text = """
                    Game has ended! (${mentionPlayer(match.playerX.id, match.playerX.firstName)} vs ${mentionPlayer(match.playerO.id, match.playerO.firstName)})
                    
                    The winner is <b>${match.playerX.firstName}</b>!
                """.trimIndent()

                bot.editMessage(callbackQuery, text, match.getKeyboard(true))
                matches.removeMatch(match.id)

            }

            MatchResult.O_WINS -> {
                val text = """
                    Game has ended! (${mentionPlayer(match.playerX.id, match.playerX.firstName)} vs ${mentionPlayer(match.playerO.id, match.playerO.firstName)})
                    
                    The winner is <b>${match.playerO.firstName}</b>!
                """.trimIndent()

                bot.editMessage(callbackQuery, text, match.getKeyboard(true))
                matches.removeMatch(match.id)
            }

            MatchResult.DRAW -> {
                val text = """
                    Game has ended! (${mentionPlayer(match.playerX.id, match.playerX.firstName)} vs ${mentionPlayer(match.playerO.id, match.playerO.firstName)})
                    
                    Nobody has won!
                """.trimIndent()

                bot.editMessage(callbackQuery, text, match.getKeyboard(true))
                matches.removeMatch(match.id)
            }
        }
    }


}