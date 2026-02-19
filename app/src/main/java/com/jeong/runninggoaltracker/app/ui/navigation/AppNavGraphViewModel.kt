package com.jeong.runninggoaltracker.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.jeong.runninggoaltracker.shared.navigation.AuthRoute
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppNavGraphUiState(
    val startDestination: NavigationRoute = AuthRoute.Onboarding
)

@HiltViewModel
class AppNavGraphViewModel @Inject constructor(
    startDestinationProvider: StartDestinationProvider
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            AppNavGraphUiState(startDestination = startDestinationProvider.startDestination())
        )
    val uiState: StateFlow<AppNavGraphUiState> = _uiState.asStateFlow()
}

class StartDestinationProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun startDestination(): NavigationRoute =
        if (firebaseAuth.currentUser?.displayName?.isNotBlank() == true) {
            MainNavigationRoute.Home
        } else {
            AuthRoute.Onboarding
        }
}
