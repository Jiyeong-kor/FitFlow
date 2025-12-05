package com.jeong.runninggoaltracker.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.repository.RunningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val weeklyGoalKm: Double? = null,
    val totalThisWeekKm: Double = 0.0,
    val recordCountThisWeek: Int = 0,
    val progress: Float = 0f
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: RunningRepository
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HomeUiState> =
        combine(
            repository.getGoal(),
            repository.getAllRecords()
        ) { goal, records ->
            buildUiState(goal, records)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildUiState(goal: RunningGoal?, records: List<RunningRecord>): HomeUiState {
        val startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY)

        val thisWeekRecords = records.filter { record ->
            val date = runCatching { LocalDate.parse(record.date) }.getOrNull()
            date != null && !date.isBefore(startOfWeek)
        }

        val totalKm = thisWeekRecords.sumOf { it.distanceKm }
        val count = thisWeekRecords.size
        val weeklyGoalKm = goal?.weeklyGoalKm

        val progress = if (weeklyGoalKm != null && weeklyGoalKm > 0.0) {
            (totalKm / weeklyGoalKm)
                .coerceIn(0.0, 1.0)
                .toFloat()
        } else {
            0f
        }

        return HomeUiState(
            weeklyGoalKm = weeklyGoalKm,
            totalThisWeekKm = totalKm,
            recordCountThisWeek = count,
            progress = progress
        )
    }
}
