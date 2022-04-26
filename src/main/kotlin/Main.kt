import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    runBlocking {
        val pixiv = KPixiv.getInstance()
        val data = pixiv.apiClient.getUserDetails(44400)

        println(data.toString())
    }
}