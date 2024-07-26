package callback

import database.Database
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import locale.Languages
import utils.answerCallback
import utils.editMessage
import utils.getBackButton

class LangCallback(
    private val bot: Bot,
    private val database: Database
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("lang")) return

        val sender = callbackQuery.sender
        if (!database.userExists(sender.id)) database.addUser(sender.id, sender.firstName)

        val selectedLang = data.split("_")[1]
        val userLang = database.getUserLang(sender.id)

        if (selectedLang == userLang) {
            bot.answerCallback(callbackQuery, Languages.getMessage(userLang, "already_selected_lang"), true)
            return
        }

        database.setLang(sender.id, selectedLang)

        bot.editMessage(callbackQuery, Languages.getMessage(selectedLang, "lang_changed"), InlineKeyboardMarkup(
            getBackButton(selectedLang)
        ))
    }
}