package inline

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.inline.InlineQuery
import io.github.ageofwar.telejam.inline.InlineQueryHandler
import io.github.ageofwar.telejam.inline.InlineQueryResultArticle
import io.github.ageofwar.telejam.inline.InputTextMessageContent
import io.github.ageofwar.telejam.methods.AnswerInlineQuery
import io.github.ageofwar.telejam.text.Text
import utils.getStartKeyboard

class StartInlineQuery(
    private val bot: Bot
) : InlineQueryHandler {

    override fun onInlineQuery(inlineQuery: InlineQuery) {
        val article = InlineQueryResultArticle(
            inlineQuery.id,
            "Start the bot!",
            InputTextMessageContent(Text.parseHtml("Hello, what do you want to do?")),
            getStartKeyboard(),
            "No matter what you type."
        )

        val answerInlineQuery = AnswerInlineQuery()
            .inlineQuery(inlineQuery)
            .results(
                article
            )

        bot.execute(answerInlineQuery)
    }
}