import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {

    // Test UserID: 71136924
    // Test IllustID: 96916534
    // Test UgoiraID: 83865186
    // Test NovelID: 17479409
    // Test CommentID: 76108978

    runBlocking {
        val pixiv = KPixiv.getInstance()
        val data = pixiv.apiClient.getBookmarkTags(71136924, true)

        println(data.toString())
    }
}