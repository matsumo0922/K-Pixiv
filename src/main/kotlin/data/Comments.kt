package data

import kotlinx.serialization.Serializable

@Serializable
data class Comments(
    override val values: List<Comment> = emptyList(), override val nextUrl: String? = null
) : PixivListData<Comment>