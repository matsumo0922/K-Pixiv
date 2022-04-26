package tool

import java.net.URLEncoder

object UrlTool {

    fun urlEncode(list: List<Pair<String, String>>): String {
        return list.joinToString(separator = "&") { "${URLEncoder.encode(it.first, Charsets.UTF_8)}=${URLEncoder.encode(it.second, Charsets.UTF_8)}" }
    }

}