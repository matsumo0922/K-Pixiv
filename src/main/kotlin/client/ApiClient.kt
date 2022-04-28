package client

import data.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.coroutines.CoroutineContext

class ApiClient private constructor(
    private val config: Config,
    private val callback: OnRequireCallback,
    private val formatter: Json,
    ): Client(), CoroutineScope {

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

        HttpResponseValidator {
            validateResponse {
                println("DEBUG: $it")
            }
        }
    }

    /**
     * Test OK
     */
    suspend fun getUserDetails(userId: Long): UserDetail? {
        return httpClient.get {
            url("${Endpoint.API}/v1/user/detail")

            parameter("filter", "for_android")
            parameter("user_id", userId)
        }.parse()
    }

    /**
     * Test OK
     */
    suspend fun getUserIllustBookmarks(userId: Long, restrict: Restrict = Restrict.Public, nextUrl: String? = null): List<Illust> {
        val illusts = httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/bookmarks/illust")

                parameter("user_id", userId)
                parameter("restrict", restrict.value)
            } else {
                url(nextUrl)
            }
        }.parse<Illusts>() ?: return emptyList()

        return mutableListOf<Illust>().apply {
            addAll(illusts.values)
            if(!illusts.nextUrl.isNullOrBlank()) addAll(getUserIllustBookmarks(userId, restrict, illusts.nextUrl))
        }
    }

    /**
     * Test OK
     */
    suspend fun getUserNovelBookmarks(userId: Long, restrict: Restrict = Restrict.Public, nextUrl: String? = null): List<Illust> {
        val illusts = httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/bookmarks/novel")

                parameter("user_id", userId)
                parameter("restrict", restrict.value)
            } else {
                url(nextUrl)
            }
        }.parse<Illusts>() ?: return emptyList()

        return mutableListOf<Illust>().apply {
            addAll(illusts.values)
            if(!illusts.nextUrl.isNullOrBlank()) addAll(getUserNovelBookmarks(userId, restrict, illusts.nextUrl))
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
                parameter("filter", "for_android")
                parameter("type", type.value)
            } else {
                url(nextUrl)
            }
        }.parse<Illusts>() ?: return emptyList()

        return mutableListOf<Illust>().apply {
            addAll(illusts.values)
            if(!illusts.nextUrl.isNullOrBlank()) addAll(getUserIllusts(userId, type, illusts.nextUrl))
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
                parameter("filter", "for_android")
            } else {
                url(nextUrl)
            }
        }.parse<Novels>() ?: return emptyList()

        return mutableListOf<Novel>().apply {
            addAll(novels.values)
            if(!novels.nextUrl.isNullOrBlank()) addAll(getUserNovels(userId, novels.nextUrl))
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
                parameter("filter", "for_android")
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
                parameter("filter", "for_android")
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
                parameter("filter", "for_android")
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
                parameter("filter", "for_android")
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
    suspend fun getTrandTags(): List<TrendTag> {
        return httpClient.get {
            url("${Endpoint.API}/v1/trending-tags/illust")
            parameter("filter", "for_android")
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
            if (!users.nextUrl.isNullOrBlank()) addAll(getFollowingUsers(userId, restrict, users.nextUrl))
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
                parameter("filter", "for_android")
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
                parameter("filter", "for_android")
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
    suspend fun getNewIllusts(type: IllustType, nextUrl: String? = null): Illusts? {
        return httpClient.get {
            url("${Endpoint.API}/v1/illust/new")
            parameter("filter", "for_android")
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
            parameter("filter", "for_android")
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
            parameter("filter", "for_android")
            parameter("illust_id", illustId)
        }.parse<JsonObject>() ?: return null

        return formatter.decodeFromJsonElement(illust["illust"] ?: return null)
    }

    /**
     * Test OK
     * 小説をHTMLで取得
     */
    suspend fun getNovelHtml(novelId: Int): String {
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
            if (!comment.nextUrl.isNullOrBlank()) addAll(getComments(illustId, comment.nextUrl))
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
            if (!comment.nextUrl.isNullOrBlank()) addAll(getCommentReplies(commentId, comment.nextUrl))
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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    abstract class OnRequireCallback {
        abstract suspend fun onRequireAccessToken(): String
    }
}