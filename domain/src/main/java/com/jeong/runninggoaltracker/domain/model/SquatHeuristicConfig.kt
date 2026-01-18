package com.jeong.runninggoaltracker.domain.model

data class SquatHeuristicConfig(
    val enableHeelRiseProxy: Boolean,
    val heelRiseRatioThreshold: Float,
    val enableKneeForwardProxy: Boolean,
    val kneeForwardRatioThreshold: Float
)
