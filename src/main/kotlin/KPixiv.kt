import client.ApiClient
import client.AuthClient
import client.Config
import data.AuthCode
import data.UserAccount
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class KPixiv private constructor(private val config: Config) {

    companion object {
        private var instance: KPixiv? = null

        fun getInstance(config: Config = Config()): KPixiv {
            return instance ?: KPixiv(config).also { instance = it }
        }
    }

    private val formatter = Json {
        isLenient = true
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    private val apiCallback = object : ApiClient.OnRequireCallback() {
        override suspend fun onRequireAccessToken(): String {
            return authClient.getActiveAccountSync()?.accessToken ?: ""
        }
    }

    val authClient = AuthClient.getInstance(config, formatter)
    val apiClient = ApiClient.getInstance(config, apiCallback, formatter)
}