package database

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Sorts
import org.bson.Document
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class MongoWrapper {
    private val client: MongoClient = MongoClients.create("mongodb://localhost:27017")
    private val database: MongoDatabase = client.getDatabase("tictactoeBot")
    private var collection: MongoCollection<Document> = database.getCollection("users")

    fun addUser(id: Long, firstName: String) {
        val document = Document("id", id)
        document.append("firstName", firstName)
        document.append("locale", "en")
        document.append("matchWon", 0)
        document.append("matchLost", 0)
        document.append("ratio", "NDA")
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
        val ratio = user?.getString("ratio") ?: "NDA"

        return UserInfo(id, firstName, locale, matchWon, matchLost, ratio)
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
                    ratio = doc.getString("ratio")
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

            val updatedMatchWon = if (won) matchWon + 1 else matchWon
            val updatedMatchLost = if (!won) matchLost + 1 else matchLost

            val ratio = if (updatedMatchLost > 0) updatedMatchWon.toDouble() / updatedMatchLost else updatedMatchWon.toDouble()

            val symbols = DecimalFormatSymbols().apply {
                decimalSeparator = '.'
            }

            val decimalFormat = DecimalFormat("#.00", symbols)
            val formattedRatio = decimalFormat.format(ratio)

            val update = BasicDBObject("\$set", BasicDBObject(
                mapOf(
                    "matchWon" to updatedMatchWon,
                    "matchLost" to updatedMatchLost,
                    "ratio" to formattedRatio
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
    val ratio: String,
)