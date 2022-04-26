package client

import data.AuthCode
import data.UserAccount
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import tool.UrlTool
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.Base64


class AuthClient private constructor(
    private val config: Config,
    private val formatter: Json,
    ): Client() {

    companion object {
        private var instance: AuthClient? = null

        fun getInstance(config: Config, formatter: Json = Json): AuthClient {
            return instance ?: AuthClient(config, formatter).also { instance = it }
        }
    }

    private val httpClient = HttpClient(CIO) {
        defaultRequest {
            header("User-Agent", "PixivAndroidApp/5.0.234 (Android 11; Pixel 5)")
        }

        install(ContentNegotiation) {
            json(formatter)
        }
    }

    fun isInitialized(): Boolean {
        return config.accountFile.exists()
    }

    fun getLatestAccount(): UserAccount? {
        if(!isInitialized()) return null

        val json = config.accountFile.readText()
        return formatter.decodeFromString(UserAccount.serializer(), json)
    }

    fun getActiveAccount(): UserAccount? {
        val account = getLatestAccount() ?: return null
        return if(isActiveAccount(account)) account else null
    }

    suspend fun getActiveAccountSync(): UserAccount? {
        val account = getLatestAccount() ?: return null
        return refreshAccount(account.refreshToken)
    }

    fun getAuthCode(): AuthCode {
        val codeVerifier = getCodeVerifier(32)
        val codeChallenge = getCodeChallenge(codeVerifier)
        val params = listOf(
            "code_challenge" to codeChallenge,
            "code_challenge_method" to "S256",
            "client" to "pixiv-android"
        )

        return AuthCode(
            code = "",
            codeVerifier = codeVerifier,
            codeChallenge = codeChallenge,
            url = "${Endpoint.AUTH}/web/v1/login?${UrlTool.urlEncode(params)}"
        )
    }

    suspend fun initAccount(authCode: AuthCode): UserAccount? {
        return httpClient.submitForm(
            url = "${Endpoint.AUTH}/auth/token",
            formParameters = Parameters.build {
                append("client_id", CLIENT_ID)
                append("client_secret", CLIENT_SECRET)
                append("code", authCode.code)
                append("code_verifier", authCode.codeVerifier)
                append("grant_type", "authorization_code")
                append("include_policy", true.toString())
                append("redirect_uri", "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback")
            }
        ).parse {
            if(it != null) saveAccount(it)
        }
    }

    suspend fun refreshAccount(refreshToken: String): UserAccount? {
        return httpClient.submitForm(
            url = "${Endpoint.AUTH}/auth/token",
            formParameters = Parameters.build {
                append("client_id", CLIENT_ID)
                append("client_secret", CLIENT_SECRET)
                append("include_policy", true.toString())
                append("grant_type", "refresh_token")
                append("refresh_token", refreshToken)
            }
        ).parse {
            if(it != null) saveAccount(it)
        }
    }

    private fun getCodeVerifier(length: Int): String {
        return (('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('-', '.', '_', '~')).let { allowedChars ->
            (0 until length).map { allowedChars.random() }.joinToString(separator = "")
        }
    }

    private fun getCodeChallenge(code: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(code.toByteArray(Charsets.US_ASCII))
        val codeChallenge = Base64.getEncoder().encodeToString(bytes)
        return codeChallenge.replace("=", "").replace("\\", "-").replace("/", "_")
    }

    private fun saveAccount(userAccount: UserAccount) {
        val json = formatter.encodeToString(UserAccount.serializer(), userAccount)
        config.accountFile.writeText(json)
    }

    private fun isActiveAccount(userAccount: UserAccount): Boolean {
        return userAccount.acquisitionTime.plusSeconds(userAccount.expiresIn.toLong()).isAfter(LocalDateTime.now())
    }
}