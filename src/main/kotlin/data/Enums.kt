package data

enum class Restrict(val value: String) {
    Public("public"),
    Private("private"),
}

enum class IllustType(val value: String) {
    ILLUST("illust"),
    MANGA("manga"),
}

enum class RankingMode(val value: String) {
    DAY("day"),
    DAY_R18("day_r18"), DAY_MALE("day_male"), DAY_MALE_R18("day_male_r18"), DAY_FEMALE("day_female"), DAY_FEMALE_R18("day_female_r18"), WEEK_ORIGINAL("week_original"), WEEK_ROOKIE("week_rookie"), WEEK(
        "week"
    ),
    WEEK_R18("week_r18"), MONTH("month"),
}

enum class SearchSort(val value: String) {
    DATE_DESC("date_desc"), DATE_ASC("date_asc"),
}

enum class SearchTarget(val value: String) {
    PARTIAL_MATCH_FOR_TAGS("partial_match_for_tags"),   // タグ部分一致
    EXACT_MATCH_FOR_TAGS("exact_match_for_tags"),       // タグ完全一致
    TITLE_AND_CAPTION("title_and_caption"),             // タイトルと説明
}