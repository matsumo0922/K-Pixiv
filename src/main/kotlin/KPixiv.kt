import client.ApiClient
import client.AuthClient
import client.Config
import kotlinx.serialization.json.Json

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
            return authClient.getActiveAccountSync()?.accessToken ?: throw IllegalStateException("Could not find account info. [${config.accountFile.absolutePath}] is not exist.")
        }
    }

    val authClient = AuthClient.getInstance(config, formatter)
    val apiClient = ApiClient.getInstance(config, apiCallback, formatter)
}