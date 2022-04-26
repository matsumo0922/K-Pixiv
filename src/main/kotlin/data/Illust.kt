package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Illust(
    val caption: String = "",
    @SerialName("create_date")
    val createDate: String = "",
    val height: Int = 0,
    val id: Int = 0,
    @SerialName("image_urls")
    val imageUrls: IllustUrl = IllustUrl(),
    @SerialName("is_bookmarked")
    val isBookmarked: Boolean = false,
    @SerialName("is_muted")
    val isMuted: Boolean = false,
    @SerialName("meta_pages")
    val metaPages: List<IllustUrl> = listOf(),
    @SerialName("meta_single_page")
    val metaSinglePage: MetaSinglePage = MetaSinglePage(),
    @SerialName("page_count")
    val pageCount: Int = 0,
    val restrict: Int = 0,
    @SerialName("sanity_level")
    val sanityLevel: Int = 0,
    val tags: List<Tag> = listOf(),
    val title: String = "",
    val tools: List<String> = listOf(),
    @SerialName("total_bookmarks")
    val totalBookmarks: Int = 0,
    @SerialName("total_view")
    val totalView: Int = 0,
    val type: String = "",
    val user: User = User(),
    val visible: Boolean = false,
    val width: Int = 0,
    @SerialName("x_restrict")
    val xRestrict: Int = 0
) {
    @Serializable
    data class MetaSinglePage(
        @SerialName("original_image_url")
        val originalImageUrl: String = ""
    )
}