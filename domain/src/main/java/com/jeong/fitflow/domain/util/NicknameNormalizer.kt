package com.jeong.fitflow.domain.util

import java.util.Locale

object NicknameNormalizer {
    fun normalize(nickname: String): String = nickname.trim().lowercase(Locale.ROOT)
}
