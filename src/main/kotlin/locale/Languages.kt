package locale

import java.util.*

object Languages {
    private val bundles: MutableMap<String, ResourceBundle> = mutableMapOf()

    init {
        bundles["it"] = ResourceBundle.getBundle("messages_it")
        bundles["en"] = ResourceBundle.getBundle("messages_en")
    }

    fun getMessage(language: String, key: String): String {
        val bundle = bundles[language] ?: bundles["en"]!!
        return bundle.getString(key)
    }
}

