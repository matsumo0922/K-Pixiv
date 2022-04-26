package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IllustUrl(
    val large: String = "",
    val medium: String = "",
    @SerialName("square_medium")
    val squareMedium: String = ""
)