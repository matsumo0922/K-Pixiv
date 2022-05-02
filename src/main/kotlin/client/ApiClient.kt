package client

import data.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.coroutines.CoroutineContext

class ApiClient private constructor(
    override val config: Config,
    private val callback: OnRequireCallback,
    private val formatter: Json,
) : Client(config), CoroutineScope {

    companion object {
        private var instance: ApiClient? = null

        fun getInstance(config: Config, callback: OnRequireCallback, formatter: Json = Json): ApiClient {
            return instance ?: ApiClient(config, callback, formatter).also { instance = it }
        }
    }

    private val httpClient = HttpClient(CIO) {
        defaultRequest {
            runBlocking {
                header("app-os", "ios")
                header("app-os-version", "14.6")
                header("User-Agent", "PixivIOSApp/7.13.3 (iOS 14.6; iPhone13,2)")
                header("Authorization", "Bearer ${callback.onRequireAccessToken()}")
            }
        }

        install(ContentNegotiation) {
            json(formatter)
        }
    }

    /**
     * Test OK
     * ユーザーの詳細を取得する
     */
    suspend fun getUserDetail(userId: Long): UserDetail? {
        return httpClient.get {
            url("${Endpoint.API}/v1/user/detail")

            parameter("filter", config.deviceType.value)
            parameter("user_id", userId)
        }.parse()
    }

    /**
     * Test OK
     * ユーザーがブックマークに登録しているイラストを取得する
     */
    suspend fun getUserIllustBookmarks(userId: Long, restrict: Restrict = Restrict.Public, nextUrl: String? = null): List<Illust> {
        val result = httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/bookmarks/illust")

                parameter("user_id", userId)
                parameter("restrict", restrict.value)
            } else {
                url(nextUrl)
            }
        }.parse<Illusts>() ?: return emptyList()

        return mutableListOf<Illust>().apply {
            addAll(result.values)
            addAll(getUserIllustBookmarks(userId, restrict, result.nextUrl ?: return@apply))
        }
    }

    /**
     * Test OK
     */
    suspend fun getUserNovelBookmarks(userId: Long, restrict: Restrict = Restrict.Public, nextUrl: String? = null): List<Novel> {
        val result = httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/bookmarks/novel")

                parameter("user_id", userId)
                parameter("restrict", restrict.value)
            } else {
                url(nextUrl)
            }
        }.parse<Novels>() ?: return emptyList()

        return mutableListOf<Novel>().apply {
            addAll(result.values)
            addAll(getUserNovelBookmarks(userId, restrict, result.nextUrl ?: return@apply))
        }
    }

    /**
     * Test OK
     * ユーザーが投稿したイラストを取得する
     */
    suspend fun getUserIllusts(userId: Long, type: IllustType, nextUrl: String? = null): List<Illust> {
        val illusts = httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/illusts")

                parameter("user_id", userId)
                parameter("filter", config.deviceType.value)
                parameter("type", type.value)
            } else {
                url(nextUrl)
            }
        }.parse<Illusts>() ?: return emptyList()

        return mutableListOf<Illust>().apply {
            addAll(illusts.values)
            addAll(getUserIllusts(userId, type, illusts.nextUrl ?: return@apply))
        }
    }

    /**
     * Test OK
     * ユーザーが投稿した小説を取得する
     */
    suspend fun getUserNovels(userId: Long, nextUrl: String? = null): List<Novel> {
        val novels = httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/novels")

                parameter("user_id", userId)
                parameter("filter", config.deviceType.value)
            } else {
                url(nextUrl)
            }
        }.parse<Novels>() ?: return emptyList()

        return mutableListOf<Novel>().apply {
            addAll(novels.values)
            addAll(getUserNovels(userId, novels.nextUrl ?: return@apply))
        }
    }

    /**
     * Test OK
     * おすすめのイラストを取得する
     */
    suspend fun getRecommendedIllusts(type: IllustType, nextUrl: String? = null): Illusts? {
        return httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/${type.value}/recommended")
                parameter("filter", config.deviceType.value)
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test OK
     * おすすめの小説を取得する
     */
    suspend fun getRecommendedNovels(nextUrl: String? = null): Novels? {
        return httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/novel/recommended")
                parameter("filter", config.deviceType.value)
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test OK
     * おすすめのユーザーを取得する
     */
    suspend fun getRecommendedUsers(nextUrl: String? = null): Users? {
        return httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/recommended")
                parameter("filter", config.deviceType.value)
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test OK
     * イラストのランキングを取得する
     */
    suspend fun getRanking(mode: RankingMode, nextUrl: String? = null): Illusts? {
        return httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/illust/ranking")
                parameter("filter", config.deviceType.value)
                parameter("mode", mode.value)
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test OK
     * トレンドのタグを取得する
     */
    suspend fun getTrendTags(): List<TrendTag> {
        return httpClient.get {
            url("${Endpoint.API}/v1/trending-tags/illust")
            parameter("filter", config.deviceType.value)
        }.parse<TrendTags>()?.trendTags ?: emptyList()
    }

    /**
     * Test OK
     * フォローしている人を取得
     */
    suspend fun getFollowingUsers(userId: Long, restrict: Restrict = Restrict.Public, nextUrl: String? = null): List<Users.UserPreview> {
        val users = httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v1/user/following")

                parameter("user_id", userId)
                parameter("restrict", restrict.value)
            } else {
                url(nextUrl)
            }
        }.parse<Users>() ?: return emptyList()

        return mutableListOf<Users.UserPreview>().apply {
            addAll(users.values)
            addAll(getFollowingUsers(userId, restrict, users.nextUrl ?: return@apply))
        }
    }

    /**
     * Test CAUTION -> TODO: meta_pagesの挙動を確認する必要あり
     * フォローしてる人の新着イラストを取得する？（is_followed=falseの場合もある。意味分からん。）
     */
    suspend fun getFollowerNewIllusts(restrict: Restrict? = null, nextUrl: String? = null): Illusts? {
        return httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v2/illust/follow")
                parameter("filter", config.deviceType.value)
                parameter("restrict", restrict?.value ?: "all")
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test CAUTION -> TODO: meta_pagesの挙動を確認する必要あり
     * フォローしてる人の新着小説を取得する？（is_followed=falseの場合もある。意味わからん。）
     */
    suspend fun getFollowerNewNovels(restrict: Restrict? = null, nextUrl: String? = null): Novels? {
        return httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v1/novel/follow")
                parameter("filter", config.deviceType.value)
                parameter("restrict", restrict?.value ?: "all")
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test OK
     * みんなの新着イラストを取得
     */
    suspend fun getNewIllusts(type: IllustType): Illusts? {
        return httpClient.get {
            url("${Endpoint.API}/v1/illust/new")
            parameter("filter", config.deviceType.value)
            parameter("content_type", type.value)
        }.parse()
    }

    /**
     * Test OK
     * みんなの新着小説を取得
     */
    suspend fun getNewNovels(): Novels? {
        return httpClient.get {
            url("${Endpoint.API}/v1/novel/new")
        }.parse()
    }

    /**
     * Test OK
     * 関連イラストを取得
     */
    suspend fun getIllustRelated(illustId: Long): Illusts? {
        return httpClient.get {
            url("${Endpoint.API}/v2/illust/related")
            parameter("filter", config.deviceType.value)
            parameter("illust_id", illustId)
        }.parse()
    }

    /**
     * Test OK
     * イラストの詳細を取得
     */
    suspend fun getIllustDetail(illustId: Long): Illust? {
        val illust = httpClient.get {
            url("${Endpoint.API}/v1/illust/detail")
            parameter("filter", config.deviceType.value)
            parameter("illust_id", illustId)
        }.parse<JsonObject>() ?: return null

        return formatter.decodeFromJsonElement(illust["illust"] ?: return null)
    }

    /**
     * Test OK
     * 小説をHTMLで取得
     */
    suspend fun getNovelHtml(novelId: Long): String {
        return httpClient.get {
            url("${Endpoint.API}/webview/v1/novel")
            parameter("id", novelId)
        }.bodyAsText()
    }

    /**
     * Test OK
     * うごイラの詳細を取得
     */
    suspend fun getUgoiraMetadata(illustId: Long): UgoiraMetadata? {
        val metadata = httpClient.get {
            url("${Endpoint.API}/v1/ugoira/metadata")
            parameter("illust_id", illustId)
        }.parse<JsonObject>() ?: return null

        return formatter.decodeFromJsonElement(metadata["ugoira_metadata"] ?: return null)
    }

    /**
     * Test OK
     * コメントを取得
     */
    suspend fun getComments(illustId: Long, nextUrl: String? = null): List<Comment> {
        val comment = httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v3/illust/comments")
                parameter("illust_id", illustId)
            } else {
                url(nextUrl)
            }
        }.parse<Comments>() ?: return emptyList()

        return mutableListOf<Comment>().apply {
            addAll(comment.values)
            addAll(getComments(illustId, comment.nextUrl ?: return@apply))
        }
    }

    /**
     * Test OK
     * コメントに対する返信を取得
     */
    suspend fun getCommentReplies(commentId: Long, nextUrl: String? = null): List<Comment> {
        val comment = httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v2/illust/comment/replies")
                parameter("comment_id", commentId)
            } else {
                url(nextUrl)
            }
        }.parse<Comments>() ?: return emptyList()

        return mutableListOf<Comment>().apply {
            addAll(comment.values)
            addAll(getCommentReplies(commentId, comment.nextUrl ?: return@apply))
        }
    }

    /**
     * Test OK
     * 検索時のオートコンプリート候補を取得
     */
    suspend fun getSearchAutoComplete(word: String, isMergeKeyword: Boolean = true): List<Tag> {
        val autoComplete = httpClient.get {
            url("${Endpoint.API}/v2/search/autocomplete")
            parameter("merge_plain_keyword_results", isMergeKeyword)
            parameter("word", word)
        }.parse<JsonObject>() ?: return emptyList()

        return formatter.decodeFromJsonElement(autoComplete["tags"] ?: return emptyList())
    }

    /**
     * Test CAUTION -> TODO: start_date,end_dateの同時指定が必須の可能性
     * キーワードに一致するイラストを検索
     */
    suspend fun searchIllust(searchConfig: SearchConfig, nextUrl: String? = null): SearchIllusts? {
        return httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v1/search/illust")
                parameter("filter", config.deviceType.value)
                parameter("word", searchConfig.keyword)
                parameter("sort", searchConfig.sort.value)
                parameter("search_target", searchConfig.target.value)
                parameter("include_translated_tag_results", searchConfig.isIncludeTranslatedTag)
                parameter("merge_plain_keyword_results", searchConfig.isMergePlainKeyword)

                searchConfig.startDate?.let { parameter("start_date", it.toString()) }
                searchConfig.endDate?.let { parameter("end_date", it.toString()) }
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test CAUTION -> TODO: start_date,end_dateの同時指定が必須の可能性
     * キーワードに一致する小説を検索
     */
    suspend fun searchNovel(searchConfig: SearchConfig, nextUrl: String? = null): SearchNovels? {
        return httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v1/search/novel")
                parameter("filter", config.deviceType.value)
                parameter("word", searchConfig.keyword)
                parameter("sort", searchConfig.sort.value)
                parameter("search_target", searchConfig.target.value)
                parameter("include_translated_tag_results", searchConfig.isIncludeTranslatedTag)
                parameter("merge_plain_keyword_results", searchConfig.isMergePlainKeyword)

                searchConfig.startDate?.let { parameter("start_date", it.toString()) }
                searchConfig.endDate?.let { parameter("end_date", it.toString()) }
            } else {
                url(nextUrl)
            }
        }.parse()
    }

    /**
     * Test OK
     * キーワードに一致するユーザーを検索
     */
    suspend fun searchUser(keyword: String): Users {
        return httpClient.get {
            url("${Endpoint.API}/v1/search/user")
            parameter("filter", config.deviceType.value)
            parameter("word", keyword)
        }.body()
    }

    /**
     * Test OK
     * ブックマークに登録しているイラストをすべて取得する
     */
    suspend fun getBookmarkedIllusts(
        userId: Long,
        restrict: Restrict = Restrict.Public,
        nextUrl: String? = null
    ): List<Illust> {
        val illusts = httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v1/user/bookmarks/illust")
                parameter("user_id", userId)
                parameter("restrict", restrict.value)
                parameter("filter", config.deviceType.value)
            } else {
                url(nextUrl)
            }
        }.parse<Illusts>() ?: return emptyList()

        return mutableListOf<Illust>().apply {
            addAll(illusts.values)
            addAll(getBookmarkedIllusts(userId, restrict, illusts.nextUrl ?: return@apply))
        }
    }


    /**
     * Test FAILED -> TODO: 何の目的に使うのか不明
     * ブックマークしているタグを取得する？
     */
    suspend fun getBookmarkedTags(
        userId: Long,
        imageType: ImageType,
        restrict: Restrict = Restrict.Public,
        nextUrl: String? = null
    ): List<BookmarkTag> {
        val tags = httpClient.get {
            if (nextUrl == null) {
                url("${Endpoint.API}/v1/user/bookmark-tags/${imageType.value}")
                parameter("user_id", userId)
                parameter("restrict", restrict.value)
            } else {
                url(nextUrl)
            }
        }.parse<BookmarkTags>() ?: return emptyList()

        return mutableListOf<BookmarkTag>().apply {
            addAll(tags.values)
            addAll(getBookmarkedTags(userId, imageType, restrict, tags.nextUrl ?: return@apply))
        }
    }

    /**
     * Test OK
     * ブックマークの詳細を取得する
     */
    suspend fun getBookmarkedDetail(illustId: Long): BookmarkDetail? {
        val detail = httpClient.get {
            url("${Endpoint.API}/v2/illust/bookmark/detail")
            parameter("illust_id", illustId)
        }.parse<JsonObject>() ?: return null

        return formatter.decodeFromJsonElement(detail["bookmark_detail"] ?: return null)
    }

    /**
     * Test OK
     * ブックマークに追加する
     */
    suspend fun addBookmark(illustId: Long, imageType: ImageType, restrict: Restrict = Restrict.Public, tags: List<String> = emptyList()): Boolean {
        return httpClient.submitForm(
            url = "${Endpoint.API}/v2/${imageType.value}/bookmark/add",
            formParameters = Parameters.build {
                append("${imageType.value}_id", illustId.toString())
                append("restrict", restrict.value)
                append("tags[]", tags.toString())
            }
        ).isSuccess()
    }

    /**
     * Test OK
     * ブックマークから削除する
     */
    suspend fun deleteBookmark(illustId: Long, imageType: ImageType): Boolean {
        return httpClient.submitForm(
            url = "${Endpoint.API}/v1/${imageType.value}/bookmark/delete",
            formParameters = Parameters.build {
                append("${imageType.value}_id", illustId.toString())
            }
        ).isSuccess()
    }

    /**
     * Test OK
     * ユーザーをフォローする
     */
    suspend fun addFollow(userId: Long, restrict: Restrict = Restrict.Public): Boolean {
        return httpClient.submitForm(
            url = "${Endpoint.API}/v1/user/follow/add",
            formParameters = Parameters.build {
                append("user_id", userId.toString())
                append("restrict", restrict.value)
            }
        ).isSuccess()
    }

    /**
     * Test OK
     * ユーザーのフォローを解除する
     */
    suspend fun deleteFollow(userId: Long): Boolean {
        return httpClient.submitForm(
            url = "${Endpoint.API}/v1/user/follow/delete",
            formParameters = Parameters.build {
                append("user_id", userId.toString())
            }
        ).isSuccess()
    }

    suspend fun addComment(illustId: Long, comment: String, parentCommentId: Int? = null): Comment? {
        val result = httpClient.submitForm(
            url = "${Endpoint.API}/v1/illust/comment/add",
            formParameters = Parameters.build {
                append("illust_id", illustId.toString())
                append("comment", comment)

                parentCommentId?.let { append("parent_comment_id", parentCommentId.toString()) }
            }
        ).parse<JsonObject>() ?: return null

        return formatter.decodeFromJsonElement(result["comment"] ?: return null)
    }

    suspend fun deleteComment(commentId: Long): Boolean {
        return httpClient.submitForm(
            url = "${Endpoint.API}/v1/illust/comment/delete",
            formParameters = Parameters.build {
                append("comment_id", commentId.toString())
            }
        ).isSuccess()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    abstract class OnRequireCallback {
        abstract suspend fun onRequireAccessToken(): String
    }
}