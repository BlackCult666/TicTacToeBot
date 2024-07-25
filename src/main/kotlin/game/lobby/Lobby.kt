package game.lobby

import io.github.ageofwar.telejam.users.User

class Lobby(
    val id: String,
    val host: User
) {
    var opponent : User? = null

    fun addOpponent(user: User) {
        opponent = user
    }

    fun removeOpponent() {
        opponent = null
    }
}