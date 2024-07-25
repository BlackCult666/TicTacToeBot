package game.lobby

import io.github.ageofwar.telejam.users.User
import java.util.concurrent.ConcurrentHashMap

class Lobbies {
    private val lobbies = ConcurrentHashMap<String, Lobby>()

    fun createLobby(id: String, host: User) : Lobby {
        val lobby = Lobby(id, host)
        lobbies[id] = lobby
        return lobby
    }

    fun removeLobby(id: String) = lobbies.remove(id)

    fun getLobby(id: String): Lobby? = lobbies[id]
}