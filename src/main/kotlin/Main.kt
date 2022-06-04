import client.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main(args: Array<String>) {

    runBlocking {
        val pixiv = KPixiv.getInstance(Config(debugMode = true))
        val authCode = pixiv.authClient.getAuthCode()

        withContext(Dispatchers.IO) {
            val command = "cmd /C rundll32.exe url.dll,FileProtocolHandler \"${authCode.url}\""
            Runtime.getRuntime().exec(command)
        }

        print("Enter your code > ")

        val code = readLine()
        val account = code?.let { pixiv.authClient.initAccount(authCode.apply { this.code = it }) }

        if(account != null) {
            println("Login has been successful.")
            println("Hello ${account.user.name}!")
        } else {
            println("Login failed.")
        }
    }
}