package database

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Sorts
import org.bson.Document

class Database {
    private val client: MongoClient = MongoClients.create("mongodb://localhost:27017")
    private val database: MongoDatabase = client.getDatabase("tictactoeBot")
    private var collection: MongoCollection<Document> = database.getCollection("users")

    fun addUser(id: Long, firstName: String) {
        val document = Document("id", id)
        document.append("firstName", firstName)
        document.append("locale", "en")
        document.append("matchWon", 0)
        document.append("matchLost", 0)
        document.append("actualStreak", 0)
        document.append("bestStreak", 0)
        collection.insertOne(document)
    }

    fun setLang(id: Long, lang: String) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$set", BasicDBObject("locale", lang))
        collection.updateOne(filter, update)
    }

    fun userExists(id: Long): Boolean {
        val player = collection.find(Document("id", id)).first()
        return player != null
    }

    fun getUserInfo(id: Long): UserInfo {
        val user = collection.find(Document("id", id)).first()
        val firstName = user?.getString("firstName") ?: ""
        val locale = user?.getString("locale") ?: "en"
        val matchWon = user?.getInteger("matchWon") ?: 0
        val matchLost = user?.getInteger("matchLost") ?: 0
        val actualStreak = user?.getInteger("actualStreak") ?: 0
        val bestStreak = user?.getInteger("bestStreak") ?: 0

        return UserInfo(id, firstName, locale, matchWon, matchLost, actualStreak, bestStreak)
    }


    fun updateFirstName(id: Long, newFirstName: String) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$set", Document("firstName", newFirstName))
        collection.updateOne(filter, update)
    }

    fun getTopUsers(): List<UserInfo> {
        val topPlayers = collection.find()
            .sort(Sorts.descending("matchWon"))
            .limit(4)
            .map { doc ->
                UserInfo(
                    id = doc.getLong("id"),
                    firstName = doc.getString("firstName"),
                    locale = doc.getString("locale"),
                    matchWon = doc.getInteger("matchWon"),
                    matchLost = doc.getInteger("matchLost"),
                    actualStreak = doc.getInteger("actualStreak"),
                    bestStreak = doc.getInteger("bestStreak")
                )
            }
            .toList()

        return topPlayers
    }

    fun updateStats(id: Long, won: Boolean) {
        val user = collection.find(Document("id", id)).first()
        user?.let {
            val matchWon = it.getInteger("matchWon")
            val matchLost = it.getInteger("matchLost")
            val actualStreak = it.getInteger("actualStreak")
            val bestStreak = it.getInteger("bestStreak")

            val updatedMatchWon = if (won) matchWon + 1 else matchWon
            val updatedMatchLost = if (!won) matchLost + 1 else matchLost
            val updatedActualStreak = if (won) actualStreak + 1 else 0
            val updatedBestStreak = if (actualStreak > bestStreak) actualStreak else bestStreak

            val update = BasicDBObject("\$set", BasicDBObject(
                mapOf(
                    "matchWon" to updatedMatchWon,
                    "matchLost" to updatedMatchLost,
                    "actualStreak" to updatedActualStreak,
                    "bestStreak" to updatedBestStreak
                )
            )
            )
            collection.updateOne(Document("id", id), update)
        }
    }

    fun getUserLang(id: Long) : String {
        val user = collection.find(Document("id", id)).first()
        val locale = user?.getString("locale") ?: "en"
        return locale
    }
}

data class UserInfo(
    val id: Long,
    val firstName: String,
    val locale: String,
    val matchWon: Int,
    val matchLost: Int,
    val actualStreak: Int,
    val bestStreak: Int
)