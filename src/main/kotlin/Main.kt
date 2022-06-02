import client.Config
import client.SearchConfig
import data.AuthCode
import data.Illust
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

fun main(args: Array<String>) {

    // [蛍 原神]にマッチするイラストを100回検索して、ブクマが一番多いイラストをダウンロードするサンプル

    runBlocking {
        val pixiv = KPixiv.getInstance(Config(debugMode = true))

        pixiv.authClient.initAccount(
            AuthCode(
                code = "-rZ-0-pKV4P7V3_V6f4qUFEwBx2m6wCPSrPIVPq-7Dk",
                codeVerifier = "vECwpptloi6-2a.xacEYiFqphuLnrLn0",
                codeChallenge = "HKaRtZUCVLtby+zGZ5ipzK0wfDP4kNCpSvHwat6GQQ4",
                url = "https://app-api.pixiv.net/web/v1/login?code_challenge=HKaRtZUCVLtby%2BzGZ5ipzK0wfDP4kNCpSvHwat6GQQ4&code_challenge_method=S256&client=pixiv-android"
            )
        )

        val allDataList = mutableListOf<Illust>()
        var nextUrl: String? = null

        for (i in 0 until 2000) {
            val data = pixiv.apiClient.searchIllust(SearchConfig(keyword = "蛍 R-18"), nextUrl) ?: continue

            allDataList.addAll(data.values)
            nextUrl = data.nextUrl ?: break

            delay(500)
        }

        val sortedList = allDataList.sortedByDescending { it.totalBookmarks }
        val vestIllust = sortedList.firstOrNull() ?: return@runBlocking

        for (illust in allDataList.sortedByDescending { it.totalBookmarks }.take(10)) {
            pixiv.apiClient.downloadIllust(illust.imageUrls.large, File("./${illust.id}.png"))
            println("Saved! [${vestIllust.title}]")
        }
    }
}