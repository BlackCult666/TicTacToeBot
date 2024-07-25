package utils

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup

fun getStartKeyboard() : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton("Play", "start_play"),
        CallbackDataInlineKeyboardButton("Settings", "start_settings")
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}

fun getLobbyKeyboard(id: String) : InlineKeyboardMarkup {
    val button = CallbackDataInlineKeyboardButton("Join", "lobby_${id}_join")
    return InlineKeyboardMarkup(button)
}

fun getJoinKeyboard(id: String) : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton("Start", "lobby_${id}_start"),
        CallbackDataInlineKeyboardButton("Quit", "lobby_${id}_quit")
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}