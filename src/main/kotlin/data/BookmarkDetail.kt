package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkDetail(
    @SerialName("is_bookmarked")
    val isBookmarked: Boolean = false,
    val restrict: String = "",
    val tags: List<Tag> = listOf()
)