package com.jeong.fitflow.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.jeong.fitflow.shared.navigation.MainTab
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MainNavigationViewModel @Inject constructor(mainTabItemsProvider: MainTabItemsProvider) :
    ViewModel() {

    private val _tabItemsByTab =
        MutableStateFlow<Map<MainTab, MainTabItem>>(mainTabItemsProvider.tabItemsByTab())
    val tabItemsByTab: StateFlow<Map<MainTab, MainTabItem>> = _tabItemsByTab.asStateFlow()
}
