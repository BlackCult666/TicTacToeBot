package utils

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.inline.InlineKeyboardButton
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import locale.Languages

fun getStartKeyboard(lang: String) : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton(Languages.getMessage(lang, "menu_play_button"), "start_play"),
        CallbackDataInlineKeyboardButton(Languages.getMessage(lang, "menu_lang_button"), "start_languages")
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}

fun getLobbyKeyboard(id: String, lang: String) : InlineKeyboardMarkup {
    val button = CallbackDataInlineKeyboardButton(Languages.getMessage(lang, "join_button"), "lobby_${id}_join")
    return InlineKeyboardMarkup(button)
}

fun getJoinKeyboard(id: String, lang: String) : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton(Languages.getMessage(lang, "start_game_button"), "lobby_${id}_start"),
        CallbackDataInlineKeyboardButton(Languages.getMessage(lang, "quit_button"), "lobby_${id}_quit")
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}

fun getLangKeyboard() : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton("English", "lang_en"),
        CallbackDataInlineKeyboardButton("Italiano", "lang_it"),
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}

fun getBackButton(lang: String) : InlineKeyboardButton {
    return CallbackDataInlineKeyboardButton(Languages.getMessage(lang, "back_button"), "back")
}