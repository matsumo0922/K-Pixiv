package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgoiraMetadata(
    val frames: List<Frame> = listOf(), @SerialName("zip_urls") val zipUrls: ZipUrls = ZipUrls()
) {
    @Serializable
    data class Frame(
        val delay: Int = 0, val file: String = ""
    )

    @Serializable
    data class ZipUrls(
        val medium: String = ""
    )
}