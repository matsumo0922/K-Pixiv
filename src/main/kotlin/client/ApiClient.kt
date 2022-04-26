package client

import data.*
import io.ktor.client.*
import io.ktor.client.call.*
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
    }

    suspend fun getUserDetails(userId: Long): UserDetail? {
        return httpClient.get {
            url("${Endpoint.API}/v1/user/detail")

            parameter("filter", "for_android")
            parameter("user_id", userId)
        }.parse()
    }

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