package client

import java.io.File

data class Config(
    val accountFile: File = File("./UserAccount.json"),
)