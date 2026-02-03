package com.jeong.runninggoaltracker.app.ui.privacy

import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.runtime.remember
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
    uiState: PrivacyPolicyUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onLoadStarted: () -> Unit,
    onLoadFinished: () -> Unit,
    onLoadError: () -> Unit,
    onReloadHandled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val webViewDescription = stringResource(id = R.string.privacy_policy_webview_description)
    val errorMessage = stringResource(id = R.string.privacy_policy_load_error)
    val retryLabel = stringResource(id = R.string.privacy_policy_retry)
    val loadingDescription = stringResource(id = R.string.privacy_policy_loading)

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
            if (uiState.hasError) {
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
                        onClick = onRetry
                    ) {
                        Text(text = retryLabel)
                    }
                }
            } else {
                PrivacyPolicyWebView(
                    uiState = uiState,
                    onBack = onBack,
                    onLoadStarted = onLoadStarted,
                    onLoadFinished = onLoadFinished,
                    onLoadError = onLoadError,
                    onReloadHandled = onReloadHandled,
                    webViewDescription = webViewDescription,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (uiState.isLoading && !uiState.hasError) {
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

@Composable
private fun PrivacyPolicyWebView(
    uiState: PrivacyPolicyUiState,
    onBack: () -> Unit,
    onLoadStarted: () -> Unit,
    onLoadFinished: () -> Unit,
    onLoadError: () -> Unit,
    onReloadHandled: () -> Unit,
    webViewDescription: String,
    modifier: Modifier = Modifier
) {
    val url = BuildConfig.PRIVACY_POLICY_URL
    val webViewHolder = remember { WebViewHolder() }

    BackHandler {
        val webView = webViewHolder.webView
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            onBack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewHolder.webView?.apply {
                stopLoading()
                webViewClient = WebViewClient()
                (parent as? ViewGroup)?.removeView(this)
                destroy()
            }
            webViewHolder.webView = null
        }
    }

    AndroidView(
        modifier = modifier.semantics {
            contentDescription = webViewDescription
        },
        factory = { context ->
            WebView(context).apply {
                webViewHolder.webView = this
                settings.javaScriptEnabled = false
                webViewClient = object : WebViewClient() {

                    override fun onPageStarted(
                        view: WebView?,
                        url: String?,
                        favicon: android.graphics.Bitmap?
                    ) {
                        onLoadStarted()
                    }

                    override fun onPageFinished(
                        view: WebView?,
                        url: String?
                    ) {
                        onLoadFinished()
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        if (request?.isForMainFrame != false) {
                            onLoadError()
                        }
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        if (request?.isForMainFrame != false) {
                            onLoadError()
                        }
                    }
                }
                loadUrl(url)
            }
        },
        update = { webView ->
            if (uiState.reloadToken != uiState.lastHandledReloadToken) {
                onReloadHandled()
                webView.loadUrl(url)
            } else if (webView.url != url) {
                webView.loadUrl(url)
            }
        }
    )
}

private class WebViewHolder {
    var webView: WebView? = null
}
