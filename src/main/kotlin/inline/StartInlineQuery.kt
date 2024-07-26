package inline

import database.MongoWrapper
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.inline.InlineQuery
import io.github.ageofwar.telejam.inline.InlineQueryHandler
import io.github.ageofwar.telejam.inline.InlineQueryResultArticle
import io.github.ageofwar.telejam.inline.InputTextMessageContent
import io.github.ageofwar.telejam.methods.AnswerInlineQuery
import io.github.ageofwar.telejam.text.Text
import locale.Languages
import utils.getStartKeyboard

class StartInlineQuery(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : InlineQueryHandler {

    override fun onInlineQuery(inlineQuery: InlineQuery) {
        val lang = mongoWrapper.getUserLang(inlineQuery.sender.id)
        val article = InlineQueryResultArticle(
            inlineQuery.id,
            Languages.getMessage(lang, "menu_inline_title"),
            InputTextMessageContent(Text.parseHtml(Languages.getMessage(lang, "menu_text"))),
            getStartKeyboard(lang),
            Languages.getMessage(lang, "menu_inline_description")
        )

        val answerInlineQuery = AnswerInlineQuery()
            .inlineQuery(inlineQuery)
            .cacheTime(0)
            .results(
                article
            )

        bot.execute(answerInlineQuery)
    }
}