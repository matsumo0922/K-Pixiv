package client

import io.ktor.client.call.*
import io.ktor.client.statement.*

open class Client(open val config: Config) {
    companion object {
        const val CLIENT_ID = "MOBrBDS8blbauoSck0ZfDbtuzpyT"
        const val CLIENT_SECRET = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"
    }

    object Endpoint {
        const val API = "https://app-api.pixiv.net"
        const val AUTH = "https://oauth.secure.pixiv.net"
    }

    protected suspend inline fun <reified T> HttpResponse.parse(allowRange: IntRange = 200..299, f: ((T?) -> (Unit)) = {}): T? {
        if (config.debugMode) println("[${this.status}, ${this.request.url}]")
        return (if(this.status.value in allowRange) this.body<T>() else null).also(f)
    }

    protected suspend fun HttpResponse.parseBytes(allowRange: IntRange = 200..299): ByteArray? {
        if (config.debugMode) println("[${this.status}, DOWNLOAD, ${this.request.url}]")
        return if(this.status.value in allowRange) this.readBytes() else null
    }

    protected fun HttpResponse.isSuccess(allowRange: IntRange = 200..299): Boolean {
        if (config.debugMode) println("[${this.status}, ${this.request.url}]")
        return (this.status.value in allowRange)
    }
}