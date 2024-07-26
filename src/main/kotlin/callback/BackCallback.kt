package callback

import database.Database
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import locale.Languages
import utils.editMessage
import utils.getStartKeyboard

class BackCallback(
    private val bot: Bot,
    private val database: Database
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (data != "back") return

        val lang = database.getUserLang(callbackQuery.sender.id)

        bot.editMessage(callbackQuery, Languages.getMessage(lang, "menu_text"), getStartKeyboard(lang))
    }
}