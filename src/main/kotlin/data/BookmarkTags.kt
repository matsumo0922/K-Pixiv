package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkTags(
    @SerialName("bookmark_tags")
    override val values: List<BookmarkTag> = listOf(),
    @SerialName("next_url")
    override val nextUrl: String? = null
): PixivListData<BookmarkTag>