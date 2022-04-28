package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendTag(
    val illust: Illust = Illust(), val tag: String = "", @SerialName("translated_name") val translatedName: String? = null
)