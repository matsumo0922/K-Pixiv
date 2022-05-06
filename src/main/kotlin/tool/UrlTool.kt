package tool

import java.net.URLEncoder

object UrlTool {

    fun urlEncode(list: List<Pair<String, String>>): String {
        return list.joinToString(separator = "&") {
            "${URLEncoder.encode(it.first, "UTF-8")}=${URLEncoder.encode(it.second, "UTF-8")}"
        }
    }

    fun getParameter(url: String): Map<String, String> {
        val questionIndex = url.indexOf('?')
        if (questionIndex == -1) return emptyMap()

        val result = mutableMapOf<String, String>()
        for (parameter in url.substring(questionIndex + 1).split('&')) {
            val keyValue = parameter.split('=')
            if (keyValue.size != 2) continue

            result[keyValue[0]] = keyValue[1]
        }

        return result
    }
}