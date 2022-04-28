package data


import LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UserAccount(
    @SerialName("access_token")
    val accessToken: String = "",
    @SerialName("expires_in")
    val expiresIn: Int = 0,
    @SerialName("refresh_token")
    val refreshToken: String = "",
    val scope: String = "",
    @SerialName("token_type")
    val tokenType: String = "",
    val user: LocalUser = LocalUser(), @SerialName("acquisition_time") @Serializable(with = LocalDateTimeSerializer::class) val acquisitionTime: LocalDateTime = LocalDateTime.now()
)