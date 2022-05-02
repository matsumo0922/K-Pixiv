import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {

    // Dummy UserID: 58290317

    // Test UserID: 71136924, 1309448
    // Test IllustID: 96916534
    // Test UgoiraID: 83865186
    // Test NovelID: 17479409
    // Test CommentID: 76108978
    // Test Bookmark IllustID: 67535399

    runBlocking {
        val pixiv = KPixiv.getInstance()
        val data = pixiv.apiClient.getUserNovelBookmarks(278259)

        println(data.toString())
    }
}