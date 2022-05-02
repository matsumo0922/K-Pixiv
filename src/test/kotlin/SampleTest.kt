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
            pixiv.apiClient.getUserDetail(TestData.testUserID)
        }
    }

    @Test
    fun getUserIllustBookmarksTest() {
        runBlocking {
            pixiv.apiClient.getUserIllustBookmarks(TestData.testUserID)
        }
    }

    @Test
    fun getUserNovelBookmarksTest() {
        runBlocking {
            pixiv.apiClient.getUserNovelBookmarks(TestData.testUserID)
        }
    }

    @Test
    fun getUserIllustTest() {
        runBlocking {
            pixiv.apiClient.getUserIllusts(TestData.testUserID, IllustType.ILLUST)
        }
    }

    @Test
    fun getUserNovelTest() {
        runBlocking {
            pixiv.apiClient.getUserNovels(TestData.testUserID)
        }
    }

    @Test
    fun getRecommendedUserTest() {
        runBlocking {
            pixiv.apiClient.getRecommendedUsers()
        }
    }

    @Test
    fun getRecommendedIllustsTest() {
        runBlocking {
            pixiv.apiClient.getRecommendedIllusts(IllustType.ILLUST)
        }
    }

    @Test
    fun getRecommendedNovelTest() {
        runBlocking {
            pixiv.apiClient.getRecommendedNovels()
        }
    }

    @Test
    fun getRankingTest() {
        runBlocking {
            pixiv.apiClient.getRanking(RankingMode.DAY)
        }
    }

    @Test
    fun getTrendTest() {
        runBlocking {
            pixiv.apiClient.getTrendTags()
        }
    }

    @Test
    fun getFollowingUsersTest() {
        runBlocking {
            pixiv.apiClient.getFollowingUsers(TestData.testUserID)
        }
    }

    @Test
    fun getFollowerNewIllustsTest() {
        runBlocking {
            pixiv.apiClient.getFollowerNewIllusts()
        }
    }

    @Test
    fun getFollowerNewNovelsTest() {
        runBlocking {
            pixiv.apiClient.getFollowerNewNovels()
        }
    }

    @Test
    fun getNewIllustsTest() {
        runBlocking {
            pixiv.apiClient.getNewIllusts(IllustType.ILLUST)
        }
    }

    @Test
    fun getNewNovelsTest() {
        runBlocking {
            pixiv.apiClient.getNewNovels()
        }
    }

    @Test
    fun getIllustRelatedTest() {
        runBlocking {
            pixiv.apiClient.getIllustRelated(TestData.testIllustID)
        }
    }

    @Test
    fun getIllustDetailsTest() {
        runBlocking {
            pixiv.apiClient.getIllustDetail(TestData.testIllustID)
        }
    }

    @Test
    fun getNovelHtmlTest() {
        runBlocking {
            pixiv.apiClient.getNovelHtml(TestData.testNovelID)
        }
    }

    @Test
    fun getUgoiraMetadataTest() {
        runBlocking {
            pixiv.apiClient.getUgoiraMetadata(TestData.testUgoiraID)
        }
    }

    @Test
    fun getCommentsTest() {
        runBlocking {
            pixiv.apiClient.getComments(TestData.testIllustID)
        }
    }

    @Test
    fun getCommentsRepliesTest() {
        runBlocking {
            pixiv.apiClient.getCommentReplies(TestData.testCommentID)
        }
    }

    @Test
    fun getSearchAutoCompleteTest() {
        runBlocking {
            pixiv.apiClient.getSearchAutoComplete("ソードアート")
        }
    }

    @Test
    fun searchIllustTest() {
        runBlocking {
            pixiv.apiClient.searchIllust(SearchConfig("遠坂凛"))
        }
    }

    @Test
    fun searchNovelTest() {
        runBlocking {
            pixiv.apiClient.searchNovel(SearchConfig("四宮かぐや"))
        }
    }

    @Test
    fun searchUserTest() {
        runBlocking {
            pixiv.apiClient.searchUser("ABC")
        }
    }

    @Test
    fun getBookmarkedIllustsTest() {
        runBlocking {
            pixiv.apiClient.getBookmarkedIllusts(TestData.testUserID)
        }
    }

    @Test
    fun getBookmarkedTagTest() {
        runBlocking {
            pixiv.apiClient.getBookmarkedTags(TestData.dummyUserID, ImageType.ILLUST)
        }
    }

    @Test
    fun getBookmarkedDetailTest() {
        runBlocking {
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