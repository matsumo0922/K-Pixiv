import client.Config
import client.SearchConfig
import data.IllustType
import data.ImageType
import data.RankingMode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class SampleTest {

    private val pixiv = KPixiv.getInstance(Config(debugMode = true))

    @Test
    fun getUserDetailsTest() {
        runBlocking {
            println("\nGetUserDetailsTest")
            pixiv.apiClient.getUserDetail(TestData.testUserID)
        }
    }

    @Test
    fun getUserIllustBookmarksTest() {
        runBlocking {
            println("\nGetUserIllustBookmarksTest")
            pixiv.apiClient.getUserIllustBookmarks(TestData.testUserID)
        }
    }

    @Test
    fun getUserNovelBookmarksTest() {
        runBlocking {
            println("\nGetUserNovelBookmarksTest")
            pixiv.apiClient.getUserNovelBookmarks(TestData.testUserID)
        }
    }

    @Test
    fun getUserIllustTest() {
        runBlocking {
            println("\nGetUserIllustTest")
            pixiv.apiClient.getUserIllusts(TestData.testUserID, IllustType.ILLUST)
        }
    }

    @Test
    fun getUserNovelTest() {
        runBlocking {
            println("\nGetUserNovelTest")
            pixiv.apiClient.getUserNovels(TestData.testUserID)
        }
    }

    @Test
    fun getRecommendedUserTest() {
        runBlocking {
            println("\nGetRecommendedUserTest")
            pixiv.apiClient.getRecommendedUsers()
        }
    }

    @Test
    fun getRecommendedIllustsTest() {
        runBlocking {
            println("\nGetRecommendedIllustsTest")
            pixiv.apiClient.getRecommendedIllusts(IllustType.ILLUST)
        }
    }

    @Test
    fun getRecommendedNovelTest() {
        runBlocking {
            println("\nGetRecommendedNovelTest")
            pixiv.apiClient.getRecommendedNovels()
        }
    }

    @Test
    fun getRankingTest() {
        runBlocking {
            println("\nGetRankingTest")
            pixiv.apiClient.getRanking(RankingMode.DAY)
        }
    }

    @Test
    fun getTrendTest() {
        runBlocking {
            println("\nGetTrendTest")
            pixiv.apiClient.getTrendTags()
        }
    }

    @Test
    fun getFollowingUsersTest() {
        runBlocking {
            println("\nGetFollowingUsersTest")
            pixiv.apiClient.getFollowingUsers(TestData.testUserID)
        }
    }

    @Test
    fun getFollowerNewIllustsTest() {
        runBlocking {
            println("\nGetFollowerNewIllustsTest")
            pixiv.apiClient.getFollowerNewIllusts()
        }
    }

    @Test
    fun getFollowerNewNovelsTest() {
        runBlocking {
            println("\nGetFollowerNewNovelsTest")
            pixiv.apiClient.getFollowerNewNovels()
        }
    }

    @Test
    fun getNewIllustsTest() {
        runBlocking {
            println("\nGetNewIllustsTest")
            pixiv.apiClient.getNewIllusts(IllustType.ILLUST)
        }
    }

    @Test
    fun getNewNovelsTest() {
        runBlocking {
            println("\nGetNewNovelTest")
            pixiv.apiClient.getNewNovels()
        }
    }

    @Test
    fun getIllustRelatedTest() {
        runBlocking {
            println("\nGetIllustRelatedTest")
            pixiv.apiClient.getIllustRelated(TestData.testIllustID)
        }
    }

    @Test
    fun getIllustDetailsTest() {
        runBlocking {
            println("\nGetIllustDetailsTest")
            pixiv.apiClient.getIllustDetail(TestData.testIllustID)
        }
    }

    @Test
    fun getNovelHtmlTest() {
        runBlocking {
            println("\nGetNovelHtmlTest")
            pixiv.apiClient.getNovelHtml(TestData.testNovelID)
        }
    }

    @Test
    fun getUgoiraMetadataTest() {
        runBlocking {
            println("\nGetUgoiraMetadataTest")
            pixiv.apiClient.getUgoiraMetadata(TestData.testUgoiraID)
        }
    }

    @Test
    fun getCommentsTest() {
        runBlocking {
            println("\nGetCommentTest")
            pixiv.apiClient.getComments(TestData.testIllustID)
        }
    }

    @Test
    fun getCommentsRepliesTest() {
        runBlocking {
            println("\nGetCommentsRepliesTest")
            pixiv.apiClient.getCommentReplies(TestData.testCommentID)
        }
    }

    @Test
    fun getSearchAutoCompleteTest() {
        runBlocking {
            println("\nGetSearchAutoCompleteTest")
            pixiv.apiClient.getSearchAutoComplete("ソードアート")
        }
    }

    @Test
    fun searchIllustTest() {
        runBlocking {
            println("\nSearchIllustTest")
            pixiv.apiClient.searchIllust(SearchConfig("遠坂凛"))
        }
    }

    @Test
    fun searchNovelTest() {
        runBlocking {
            println("\nSearchNovelTest")
            pixiv.apiClient.searchNovel(SearchConfig("四宮かぐや"))
        }
    }

    @Test
    fun searchUserTest() {
        runBlocking {
            println("\nSearchUserTest")
            pixiv.apiClient.searchUser("ABC")
        }
    }

    @Test
    fun getBookmarkedIllustsTest() {
        runBlocking {
            println("\nGetBookmarkedIllustsTest")
            pixiv.apiClient.getBookmarkedIllusts(TestData.testUserID)
        }
    }

    @Test
    fun getBookmarkedTagTest() {
        runBlocking {
            println("\nGetBookmarkedTagTest")
            pixiv.apiClient.getBookmarkedTags(TestData.dummyUserID, ImageType.ILLUST)
        }
    }

    @Test
    fun getBookmarkedDetailTest() {
        runBlocking {
            println("\nGetBookmarkedDetailTest")
            pixiv.apiClient.getBookmarkedDetail(TestData.dummyBookmarkIllustID)
        }
    }

    object TestData {
        const val dummyUserID = 58290317L
        const val dummyBookmarkIllustID = 67535399L

        const val testUserID = 1309448L
        const val testIllustID = 96916534L
        const val testUgoiraID = 83865186L
        const val testNovelID = 17479409L
        const val testCommentID = 76108978L
    }
}