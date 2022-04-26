package data

data class AuthCode(
    var code: String,
    val codeVerifier: String,
    val codeChallenge: String,
    val url: String
)