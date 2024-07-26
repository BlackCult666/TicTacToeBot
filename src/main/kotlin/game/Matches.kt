package game

import io.github.ageofwar.telejam.users.User
import java.util.concurrent.ConcurrentHashMap

class Matches {
    private val matches = ConcurrentHashMap<String, Match>()

    fun createMatch(id: String, host: User, playerX: User, playerO: User) : Match {
        val match = Match(id, host, playerX, playerO)
        matches[id] = match
        return match
    }

    fun removeMatch(id: String) = matches.remove(id)

    fun getMatch(id: String) : Match? = matches[id]

}