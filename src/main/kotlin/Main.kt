import data.IllustType
import data.RankingMode
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {

    // TestUser: 71136924

    runBlocking {
        val pixiv = KPixiv.getInstance()
        val data = pixiv.apiClient.getRanking(RankingMode.DAY)

        println(data.toString())
        println(data?.values?.size)
    }
}