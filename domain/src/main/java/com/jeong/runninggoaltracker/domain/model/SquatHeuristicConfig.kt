package com.jeong.runninggoaltracker.domain.model

data class SquatHeuristicConfig(
    val isHeelRiseProxyEnabled: Boolean,
    val heelRiseRatioThreshold: Float,
    val isKneeForwardProxyEnabled: Boolean,
    val kneeForwardRatioThreshold: Float
)
