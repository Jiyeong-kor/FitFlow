package com.jeong.runninggoaltracker.app.ui.navigation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jeong.runninggoaltracker.shared.navigation.AuthRoute
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class StartDestinationProviderTest {

    @Test
    fun returnsHomeWhenDisplayNameIsNotBlank() {
        val firebaseAuth = mockk<FirebaseAuth>()
        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseUser.displayName } returns "runner"
        every { firebaseAuth.currentUser } returns firebaseUser

        val provider = StartDestinationProvider(firebaseAuth)

        assertEquals(MainNavigationRoute.Home, provider.startDestination())
    }

    @Test
    fun returnsOnboardingWhenDisplayNameIsBlank() {
        val firebaseAuth = mockk<FirebaseAuth>()
        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseUser.displayName } returns "  "
        every { firebaseAuth.currentUser } returns firebaseUser

        val provider = StartDestinationProvider(firebaseAuth)

        assertEquals(AuthRoute.Onboarding, provider.startDestination())
    }
}
