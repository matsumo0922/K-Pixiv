package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val comment: String = "", val date: String = "", @SerialName("has_replies") val hasReplies: Boolean = false, val id: Int = 0, val user: User = User()
)