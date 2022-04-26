package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Users(
    @SerialName("user_previews")
    val userPreviews: List<UserPreview> = emptyList(),
    @SerialName("next_url")
    val nextUrl: String? = null,
) {
    @Serializable
    data class UserPreview(
        val user: User = User(),
        val illusts: List<Illust> = emptyList(),
        @SerialName("is_muted")
        val isMuted: Boolean = false
    )
}