package com.jeong.runninggoaltracker.app.ui.privacy

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import com.jeong.runninggoaltracker.BuildConfig
import com.jeong.runninggoaltracker.R
import com.jeong.runninggoaltracker.shared.designsystem.common.AppTopBar
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingLg
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingMd

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val url = BuildConfig.PRIVACY_POLICY_URL
    var hasError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var reloadKey by remember { mutableIntStateOf(0) }
    var lastReloadKey by remember { mutableIntStateOf(0) }
    val webViewHolder = remember { mutableStateOf<WebView?>(null) }
    val webViewDescription = stringResource(id = R.string.privacy_policy_webview_description)
    val errorMessage = stringResource(id = R.string.privacy_policy_load_error)
    val retryLabel = stringResource(id = R.string.privacy_policy_retry)
    val loadingDescription = stringResource(id = R.string.privacy_policy_loading)

    BackHandler {
        val webView = webViewHolder.value
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            onBack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewHolder.value?.apply {
                stopLoading()
                webViewClient = WebViewClient()
                (parent as? ViewGroup)?.removeView(this)
                destroy()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                titleResId = R.string.privacy_policy_title,
                fallbackTitleResId = R.string.privacy_policy_title,
                onBack = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (hasError) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(appSpacingLg()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = errorMessage)
                    Button(
                        modifier = Modifier.padding(top = appSpacingMd()),
                        onClick = {
                            hasError = false
                            isLoading = true
                            reloadKey += 1
                        }
                    ) {
                        Text(text = retryLabel)
                    }
                }
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { contentDescription = webViewDescription },
                    factory = { context ->
                        WebView(context).apply {
                            webViewHolder.value = this
                            settings.javaScriptEnabled = false
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: android.graphics.Bitmap?
                                ) {
                                    hasError = false
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    if (request?.isForMainFrame != false) {
                                        hasError = true
                                        isLoading = false
                                    }
                                }

                                override fun onReceivedHttpError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    errorResponse: WebResourceResponse?
                                ) {
                                    if (request?.isForMainFrame != false) {
                                        hasError = true
                                        isLoading = false
                                    }
                                }
                            }
                            loadUrl(url)
                        }
                    },
                    update = { webView ->
                        if (reloadKey != lastReloadKey) {
                            lastReloadKey = reloadKey
                            webView.loadUrl(url)
                        } else if (webView.url != url) {
                            webView.loadUrl(url)
                        }
                    }
                )
            }

            if (isLoading && !hasError) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.semantics {
                            contentDescription = loadingDescription
                        }
                    )
                }
            }
        }
    }
}
