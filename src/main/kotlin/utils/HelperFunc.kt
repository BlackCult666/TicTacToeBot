package utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun mentionPlayer(id: Long, name: String) : String {
    return "<a href='tg://user?id=$id'>$name</a>"
}

fun percentage(part: Int, whole: Int): String {
    if (whole == 0) {
        return "0"
    }

    val value = 100.0 * part / whole

    val symbols = DecimalFormatSymbols(Locale.ITALY)
    val df = DecimalFormat("#.##", symbols)
    return df.format(value)

}