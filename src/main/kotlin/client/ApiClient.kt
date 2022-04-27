package client

import data.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
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
     * 無限に再帰して取得してしまうので、[Illusts]型で返す
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
     * 無限に再帰して取得してしまうので、[Novels]型で返す
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
     * 無限に再帰して取得してしまうので、[Users]型で返す
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
     * 無限に再帰して取得してしまうので、[Illusts]型で返す
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
     */
    suspend fun getFollowingUsers(userId: Long, restrict: Restrict = Restrict.Public, nextUrl: String? = null): List<Users.UserPreview> {
        val users = httpClient.get {
            if(nextUrl == null) {
                url("${Endpoint.API}/v1/user/following")

                parameter("user_id", userId)
                parameter("restrict", restrict.value)
            } else {
                url(nextUrl)
            }
        }.parse<Users>() ?: return emptyList()

        return mutableListOf<Users.UserPreview>().apply {
            addAll(users.values)
            if(!users.nextUrl.isNullOrBlank()) addAll(getFollowingUsers(userId, restrict, users.nextUrl))
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    abstract class OnRequireCallback {
        abstract suspend fun onRequireAccessToken(): String
    }
}