package game

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.inline.InlineKeyboardButton
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import io.github.ageofwar.telejam.users.User

class Match(
    val id: String,
    val playerX: User,
    val playerO: User
) {
    private val board = Array(3) { Array(3) { ' ' } }

    var currentPlayer = playerX

    fun makeMove(index: Int): MatchResult {
        val row = index / 3
        val col = index % 3

        if (board[row][col] != ' ') {
            return MatchResult.INVALID_MOVE
        }

        board[row][col] = if (currentPlayer == playerX) '❌' else '⭕'
        val result = checkWin()

        if (result == MatchResult.IN_PROGRESS) {
            currentPlayer = if (currentPlayer == playerX) playerO else playerX
        }

        return result
    }

    private fun checkWin(): MatchResult {
        for (i in 0..2) {
            if ((board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != ' ') ||
                (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != ' ')) {
                return if (currentPlayer == playerX) MatchResult.X_WINS else MatchResult.O_WINS
            }
        }

        if ((board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != ' ') ||
            (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != ' ')) {
            return if (currentPlayer == playerX) MatchResult.X_WINS else MatchResult.O_WINS
        }

        if (board.all { row -> row.all { cell -> cell != ' ' } }) {
            return MatchResult.DRAW
        }

        return MatchResult.IN_PROGRESS
    }

    fun getKeyboard(rematch: Boolean): InlineKeyboardMarkup {
        val buttons = mutableListOf<InlineKeyboardButton>()
        for (i in 0..8) {
            val row = i / 3
            val col = i % 3
            val buttonText = if (board[row][col] == ' ') "-" else board[row][col].toString()
            buttons.add(CallbackDataInlineKeyboardButton(buttonText, "match_${id}_$i"))
        }

        if (rematch) buttons.add(CallbackDataInlineKeyboardButton("Play again", "start_play"))

        return InlineKeyboardMarkup.fromColumns(3, buttons)
    }
}