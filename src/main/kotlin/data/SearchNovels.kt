package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchNovels(
    @SerialName("novels") override val values: List<Illust> = emptyList(),
    @SerialName("next_url") override val nextUrl: String? = null,
    @SerialName("search_span_limit") val searchSpanLimit: Int = 0,
) : PixivListData<Illust>
