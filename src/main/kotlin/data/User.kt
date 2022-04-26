package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val name: String = "",
    val account: String = "",
    @SerialName("profile_image_urls")
    val profileImageUrls: ProfileImageUrls = ProfileImageUrls(),
    @SerialName("is_followed")
    val isFollowed: Boolean = false,
)
