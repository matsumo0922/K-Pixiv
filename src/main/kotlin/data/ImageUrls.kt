package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageUrls(
    val large: String = "",
    val medium: String = "",
    @SerialName("square_medium")
    val squareMedium: String = "",
    val original: String? = null,
)