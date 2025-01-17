package callback

import database.Database
import game.Matches
import game.lobby.Lobbies
import game.lobby.Lobby
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.users.User
import locale.Languages
import utils.*

class LobbyCallback(
    private val bot: Bot,
    private val lobbies: Lobbies,
    private val matches: Matches,
    private val database: Database
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("lobby")) return

        val sender = callbackQuery.sender
        val action = data.split("_")[2]

        val lobbyId = data.split("_")[1]
        val lobby = lobbies.getLobby(lobbyId)

        if (lobby == null) {
            bot.answerCallback(callbackQuery, Languages.getMessage("en", "expired_lobby"), true)
            return
        }

        val savedName = database.getUserInfo(sender.id).firstName
        if (savedName != sender.firstName) database.updateFirstName(sender.id, sender.firstName)

        val lang = database.getUserLang(lobby.host.id)

        when (action) {
            "join" -> handleJoin(callbackQuery, lang, sender, lobby)
            "quit" -> handleQuit(callbackQuery, lang, sender, lobby)
            "start" -> handleStart(callbackQuery, lang, sender, lobby)
        }

    }

    private fun handleJoin(callbackQuery: CallbackQuery, lang: String, sender: User, lobby: Lobby) {
        if (sender.id == lobby.host.id) {
            bot.answerCallback(callbackQuery, Languages.getMessage(lang, "join_host"), true)
            return
        }

        val text = Languages.getMessage(lang, "join_message")
            .replace("{host}", mentionPlayer(lobby.host.id, lobby.host.firstName))
            .replace("{opponent}", mentionPlayer(sender.id, sender.firstName))

        lobby.addOpponent(sender)

        bot.editMessage(callbackQuery, text, getJoinKeyboard(lobby.id, lang))
    }

    private fun handleQuit(callbackQuery: CallbackQuery, lang: String, sender: User, lobby: Lobby) {
        if (sender.id == lobby.host.id) {
            bot.answerCallback(callbackQuery, Languages.getMessage(lang, "host_quit"), true)
            return
        }

        val text = Languages.getMessage(lang, "quit_message")
            .replace("{quitter}", mentionPlayer(sender.id, sender.firstName))
            .replace("{host}", mentionPlayer(lobby.host.id, lobby.host.firstName))

        lobby.removeOpponent()

        bot.editMessage(callbackQuery, text, getLobbyKeyboard(lobby.id, lang))
    }

    private fun handleStart(callbackQuery: CallbackQuery, lang: String, sender: User, lobby: Lobby) {
        if (sender.id != lobby.host.id) {
            bot.answerCallback(callbackQuery, Languages.getMessage(lang, "not_host"), true)
            return
        }

        val players = listOf(lobby.opponent, lobby.host).shuffled()

        val xPlayer = players[0]!!
        val oPlayer = players[1]!!

        val match = matches.createMatch(lobby.id, lobby.host, xPlayer, oPlayer)

        val gameMessage = Languages.getMessage(lang, "match_started")
            .replace("{currentPlayer}", mentionPlayer(match.currentPlayer.id, match.currentPlayer.firstName))
            .replace("{xPlayer}", mentionPlayer(xPlayer.id, xPlayer.firstName))
            .replace("{oPlayer}", mentionPlayer(oPlayer.id, oPlayer.firstName))

        bot.editMessage(callbackQuery, gameMessage, match.getKeyboard(lang, false))

        lobbies.removeLobby(lobby.id)
    }
}