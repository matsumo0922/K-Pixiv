package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendTags(
    @SerialName("trend_tags") val trendTags: List<TrendTag>,
)