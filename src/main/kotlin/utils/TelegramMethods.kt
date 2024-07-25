package utils

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery
import io.github.ageofwar.telejam.methods.EditMessageText
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import io.github.ageofwar.telejam.text.Text

fun Bot.editMessage(callbackQuery: CallbackQuery, text: String, replyMarkup: InlineKeyboardMarkup? = null, query: String = "") {
    val editMessageText = EditMessageText()
        .callbackQuery(callbackQuery)
        .text(Text.parseHtml(text))
        .replyMarkup(replyMarkup)

    execute(editMessageText)

    answerCallback(callbackQuery, query, false)
}

fun Bot.answerCallback(callbackQuery: CallbackQuery, text: String, alert: Boolean) {
    val answerCallbackQuery = AnswerCallbackQuery()
        .callbackQuery(callbackQuery)
        .text(text)
        .showAlert(alert)

    execute(answerCallbackQuery)
}