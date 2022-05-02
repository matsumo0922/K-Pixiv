package client

import data.DeviceType
import java.io.File

data class Config(
    val accountFile: File = File("./UserAccount.json"),
    val deviceType: DeviceType = DeviceType.ANDROID,
    val debugMode: Boolean = false
)