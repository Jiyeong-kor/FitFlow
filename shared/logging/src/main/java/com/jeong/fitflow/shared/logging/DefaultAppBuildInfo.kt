package com.jeong.fitflow.shared.logging

import javax.inject.Inject

class DefaultAppBuildInfo @Inject constructor() : AppBuildInfo {
    override fun isDebugBuild(): Boolean = BuildConfig.DEBUG
}
