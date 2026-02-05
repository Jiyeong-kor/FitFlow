package com.jeong.runninggoaltracker.data.util

import com.google.firebase.Timestamp
import com.jeong.runninggoaltracker.data.contract.UserFirestoreFields
import com.jeong.runninggoaltracker.domain.model.AuthProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileDocumentMapperTest {

    @Test
    fun `kakao payload includes kakao oidc sub`() {
        val payload = UserProfileDocumentPayload(
            nickname = "runner",
            createdAt = Timestamp.now(),
            lastActiveAt = Timestamp.now(),
            authProvider = AuthProvider.KAKAO,
            kakaoOidcSub = "sub-123"
        )

        val map = payload.toFirestoreMap()

        assertEquals("runner", map[UserFirestoreFields.NICKNAME])
        assertEquals(AuthProvider.KAKAO.rawValue, map[UserFirestoreFields.AUTH_PROVIDER])
        assertTrue(map.containsKey(UserFirestoreFields.KAKAO_OIDC_SUB))
    }

    @Test
    fun `anonymous payload omits kakao oidc sub`() {
        val payload = UserProfileDocumentPayload(
            nickname = "runner",
            createdAt = Timestamp.now(),
            lastActiveAt = Timestamp.now(),
            authProvider = AuthProvider.ANONYMOUS,
            kakaoOidcSub = null
        )

        val map = payload.toFirestoreMap()

        assertEquals(AuthProvider.ANONYMOUS.rawValue, map[UserFirestoreFields.AUTH_PROVIDER])
        assertFalse(map.containsKey(UserFirestoreFields.KAKAO_OIDC_SUB))
    }
}
