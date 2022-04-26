package data

interface PixivListData<T> {
    val values: List<T>
    val nextUrl: String?
}