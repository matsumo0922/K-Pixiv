package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Users(
    @SerialName("user_previews")
    override val values: List<UserPreview> = emptyList(),
    @SerialName("next_url")
    override val nextUrl: String? = null,
): PixivListData<Users.UserPreview> {
    @Serializable
    data class UserPreview(
        val user: User = User(),
        val illusts: List<Illust> = emptyList(),
        @SerialName("is_muted")
        val isMuted: Boolean = false
    )
}