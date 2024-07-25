package callback

import game.Matches
import game.lobby.Lobbies
import game.lobby.Lobby
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.users.User
import utils.*

class LobbyCallback(
    private val bot: Bot,
    private val lobbies: Lobbies,
    private val matches: Matches
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("lobby")) return

        val sender = callbackQuery.sender
        val action = data.split("_")[2]

        val lobbyId = data.split("_")[1]
        val lobby = lobbies.getLobby(lobbyId)

        if (lobby == null) {
            bot.answerCallback(callbackQuery, "Lobby expired", true)
            return
        }

        when (action) {
            "join" -> handleJoin(callbackQuery, sender, lobby)
            "quit" -> handleQuit(callbackQuery, sender, lobby)
            "start" -> handleStart(callbackQuery, sender, lobby)
        }

    }

    private fun handleJoin(callbackQuery: CallbackQuery, sender: User, lobby: Lobby) {
        if (sender.id == lobby.host.id) {
            bot.answerCallback(callbackQuery, "You are the host of the game -.-", true)
            return
        }

        val joinedMessage = """
            Oh yeah, we have a new opponent!
            
            Host: ${mentionPlayer(lobby.host.id, lobby.host.firstName)}
            Opponent: ${mentionPlayer(sender.id, sender.firstName)}
        """.trimIndent()

        lobby.addOpponent(sender)

        bot.editMessage(callbackQuery, joinedMessage, getJoinKeyboard(lobby.id))
    }

    private fun handleQuit(callbackQuery: CallbackQuery, sender: User, lobby: Lobby) {
        if (sender.id == lobby.host.id) {
            bot.answerCallback(callbackQuery, "The host can't quit the game!", true)
            return
        }

        val quitMessage = """
            Oh no, ${mentionPlayer(sender.id, sender.firstName)} left the game!
            
            ${mentionPlayer(lobby.host.id, lobby.host.firstName)} is now looking for a new opponent!
        """.trimIndent()

        lobby.removeOpponent()

        bot.editMessage(callbackQuery, quitMessage, getLobbyKeyboard(lobby.id))
    }

    private fun handleStart(callbackQuery: CallbackQuery, sender: User, lobby: Lobby) {
        if (sender.id != lobby.host.id) {
            bot.answerCallback(callbackQuery, "You have to be host!", true)
            return
        }

        val players = listOf(lobby.opponent, lobby.host).shuffled()

        val match = matches.createMatch(lobby.id, players[0]!!, players[1]!!)

        val gameMessage = """
            Match has started! 
            
            Turn of ${mentionPlayer(match.currentPlayer.id, match.currentPlayer.firstName)}
        """.trimIndent()

        bot.editMessage(callbackQuery, gameMessage, match.getKeyboard(false))

        lobbies.removeLobby(lobby.id)
    }
}