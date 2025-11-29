package com.jeong.runninggoaltracker.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jeong.runninggoaltracker.domain.repository.RunningRepository
import com.jeong.runninggoaltracker.presentation.record.ActivityLogHolder
import com.jeong.runninggoaltracker.presentation.record.ActivityRecognitionStateHolder

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    repository: RunningRepository,
    onRecordClick: () -> Unit,
    onGoalClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )

    val state by viewModel.uiState.collectAsState()

    // 활동 인식 상태
    val activityState by ActivityRecognitionStateHolder.state.collectAsState()
    val activityLabel = when (activityState.label) {
        "NO_PERMISSION" -> "권한 필요"
        "REQUEST_FAILED", "SECURITY_EXCEPTION" -> "활동 감지 실패"
        "NO_RESULT", "NO_ACTIVITY", "UNKNOWN" -> "알 수 없음"
        else -> activityState.label   // RUNNING, WALKING, STILL, IN_VEHICLE 등
    }

    val activityLogs by ActivityLogHolder.logs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("러닝 목표 관리")

        // 카드 1: 현재 활동 + 주간 목표 요약
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("오늘 상태")

                Text("현재 활동 상태: $activityLabel (${activityState.confidence}%)")

                Spacer(modifier = Modifier.height(8.dp))

                if (state.weeklyGoalKm != null) {
                    Text("주간 목표: ${"%.1f".format(state.weeklyGoalKm)} km")
                } else {
                    Text("주간 목표: 설정되지 않음")
                }

                Text("이번 주 누적 거리: ${"%.1f".format(state.totalThisWeekKm)} km")
                Text("이번 주 러닝 횟수: ${state.recordCountThisWeek} 회")

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${(state.progress * 100).toInt()} % 달성")
            }
        }

        // 카드 2: 주요 액션 버튼
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("빠른 메뉴")

                Button(
                    onClick = onRecordClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("러닝 기록 추가 / 보기")
                }

                Button(
                    onClick = onGoalClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("주간 목표 설정")
                }

                Button(
                    onClick = onReminderClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("러닝 알림 설정")
                }
            }
        }

        // 카드 3: 최근 활동 로그
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("최근 활동 로그")

                if (activityLogs.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp) // 너무 길어지지 않게 제한
                    ) {
                        items(activityLogs) { log ->
                            Text("${log.time} - ${log.label} (${log.confidence}%)")
                        }
                    }
                } else {
                    Text("최근 활동이 없습니다.")
                }
            }
        }
    }
}
