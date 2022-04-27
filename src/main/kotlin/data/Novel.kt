package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Novel(
    val caption: String = "",
    @SerialName("create_date")
    val createDate: String = "",
    val id: Int = 0,
    @SerialName("image_urls")
    val imageUrls: ImageUrls = ImageUrls(),
    @SerialName("is_bookmarked")
    val isBookmarked: Boolean = false,
    @SerialName("is_muted")
    val isMuted: Boolean = false,
    @SerialName("is_mypixiv_only")
    val isMyPixivOnly: Boolean = false,
    @SerialName("is_original")
    val isOriginal: Boolean = false,
    @SerialName("is_x_restricted")
    val isXRestricted: Boolean = false,
    @SerialName("page_count")
    val pageCount: Int = 0,
    val restrict: Int = 0,
    val series: Series = Series(),
    val tags: List<Tag> = listOf(),
    @SerialName("text_length")
    val textLength: Int = 0,
    val title: String = "",
    @SerialName("total_bookmarks")
    val totalBookmarks: Int = 0,
    @SerialName("total_comments")
    val totalComments: Int = 0,
    @SerialName("total_view")
    val totalView: Int = 0,
    val user: User = User(),
    val visible: Boolean = false,
    @SerialName("x_restrict")
    val xRestrict: Int = 0
) {
    @Serializable
    data class Series(
        val id: Int = 0,
        val title: String = ""
    )
}