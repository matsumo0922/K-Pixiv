import client.Config
import client.SearchConfig
import data.Illust
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

fun main(args: Array<String>) {

    // [蛍 原神]にマッチするイラストを100回検索して、ブクマが一番多いイラストをダウンロードするサンプル

    runBlocking {
        val pixiv = KPixiv.getInstance(Config(debugMode = true))

        val allDataList = mutableListOf<Illust>()
        var nextUrl: String? = null

        for(i in 0 until 100) {
            val data = pixiv.apiClient.searchIllust(SearchConfig(keyword = "蛍 原神"), nextUrl) ?: continue

            allDataList.addAll(data.values)
            nextUrl = data.nextUrl ?: break

            delay(500)
        }

        val sortedList = allDataList.sortedByDescending { it.totalBookmarks }
        val vestIllust = sortedList.firstOrNull() ?: return@runBlocking

        pixiv.apiClient.downloadIllust(vestIllust.imageUrls.large, File("./Image.png"))

        println("Saved! [${vestIllust.title}]")
    }
}