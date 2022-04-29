package client

import data.SearchSort
import data.SearchTarget
import java.time.LocalDate

data class SearchConfig(
    val keyword: String,
    val sort: SearchSort = SearchSort.DATE_DESC,
    val target: SearchTarget = SearchTarget.PARTIAL_MATCH_FOR_TAGS,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isIncludeTranslatedTag: Boolean = true,
    val isMergePlainKeyword: Boolean = true,
)