package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Illusts(
    @SerialName("illusts")
    override val values: List<Illust> = emptyList(),
    @SerialName("next_url")
    override val nextUrl: String? = null,
): PixivListData<Illust>
