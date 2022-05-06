# K-Pixiv

[![](https://jitpack.io/v/CAIOS0922/K-Pixiv.svg)](https://jitpack.io/#CAIOS0922/K-Pixiv)

<p>K-Pixiv は Kotlin で書かれた Pixiv API の非公式ラッパーです。JVM, Androidで動作し、v2のOAuth認証にも対応しています。</p>

# How to use? 
## Step1 依存関係の設定

<p>K-Pixiv をプロジェクトに組み込むには、このリポジトリのコードを追加する必要があります。もしくは、Gradleに以下の依存関係を指定することで自動的に組み込むこともできます。</p>

```Gradle
dependencies {
       implementation 'com.github.CAIOS0922:K-Pixiv:<latest_version>'
}
```

## Step2 Pixivログインコードの取得
<p>第2世代にアップデートされたPixivAPIは以前のパスフレーズ認証は利用できなくなりました。OAuth認証に対応したためです。そのため、こちらもOAuth 2.0のPKCEを用いてログインする必要があります。この認証にはブラウザが必要となるため、JVMの方はChromeなどのデベロッパーツール、Androidの方はWebviewを用いることになります。</p>

<p>以下にAndroidでのWebviewを用いた例を示します。後ほどAndroidでのサンプルアプリを作成する予定ですので、そちらも合わせてご覧ください。</p>

```Kotlin
// K-Pixivをインスタンス化します
val pixiv = KPixiv.getInstance(this)

// PKCE用のVerifierなどを取得します
val loginCode = pixiv.authClient.getLoginCode()

// URLが読み込まれる際のリスナーとして使用します
val webClient = object : WebViewClient() {

    // Webviewでログインした後数回リダイレクトします。その度に呼び出されます
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

        // コードは「pixiv://」から始まるURLのパラメーターとしてセットしてあるため、それを取得します
        fun getCode(url: String): String? {
            return if(url.substring(0 until 8) == "pixiv://") UrlTool.getParameter(url)["code"] else null
        }

        launch {
            val code = getCode(request.url.toString()) ?: return@launch
            val account = pixiv.authClient.initAccount(this@AuthActivity, loginCode.apply { this.code = code })

            // アカウントの取得に成功
        }

        return super.shouldOverrideUrlLoading(view, request)
    }
}

binding.webView.apply {
    settings.javaScriptEnabled = true
    webViewClient = webClient

    // ログイン画面へ遷移します
    loadUrl(loginCode.url)
}
```

## Step3 Enjoy!
<p>以上で複雑な操作は終わりです。このログイン操作は初回に一回のみ必要で、以降はrefresh_tokenを用いて自動的にログインします。基本的なAPIの呼び出しは<code>Pixiv.apiClient</code>が担当します。認証やアカウント関連は<code>Pixiv.authClient</code>が担当しますが、これは初回以降使うことはないでしょう。また、これらのクラスはシングルインスタンスとなっているので、同時呼び出しの際は一考が必要です。</p>

<p>加えて、<code>Config</code>クラスを用いることで、K-Pixivにいくつかの設定をすることができます。特に重要なのが、アカウント情報を記載したJsonファイルの保存場所に関する設定です。デフォルトではカレントディレクトリの直下に保存する設定となっていますが、このjsonファイルにはaccess_tokenなどの重要事項が記載されているため、セキュリティ管理を施したディレクトリに保存する必要があります。</P>

```Kotlin
data class Config(
    // アカウント情報を記載したJsonファイル（超個人情報）
    val accountFile: File = File("./UserAccount.json"),

    // APIに要求するデバイスタイプ
    val deviceType: DeviceType = DeviceType.ANDROID,

    // デバッグ出力をONにするか否か
    val debugMode: Boolean = false
)
```

# License

<p>The source code is licensed MIT. The library content is licensed CC BY 4.0, see LICENSE.</p>

```
Copyright 2022 CAIOS

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```