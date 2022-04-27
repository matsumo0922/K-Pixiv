package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Novels(
    @SerialName("novels")
    override val values: List<Novel> = emptyList(),
    @SerialName("next_url")
    override val nextUrl: String? = null,
) : PixivListData<Novel>
