package data

import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageUrls(
    val medium: String = ""
)