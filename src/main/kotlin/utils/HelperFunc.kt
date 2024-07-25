package utils

fun mentionPlayer(id: Long, name: String) : String {
    return "<a href='tg://user?id=$id'>$name</a>"
}