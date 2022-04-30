package data

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkTag(
    val count: Int = 0,
    val name: String = ""
)