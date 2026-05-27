package com.example.physicaltraining.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.physicaltraining.ui.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WeeklyGraphPoint(
    val weekLabel: String,
    val maxWeight: Float,
    val volume: Float
)

@Composable
fun GraphScreen(viewModel: WorkoutViewModel) {
    val workoutHistory by viewModel.workoutHistory.collectAsState()

    val targetExercises = listOf(
        "스쿼트",
        "벤치프레스",
        "데드리프트",
        "오버헤드 프레스",
        "바벨 로우"
    )

    val filteredHistory = workoutHistory.filter { history ->
        targetExercises.any { target ->
            history.exerciseName.contains(target)
        }
    }

    val weeklyGraphData = filteredHistory
        .groupBy { history ->
            SimpleDateFormat("yyyy-ww", Locale.getDefault())
                .format(Date(history.completedAt))
        }
        .map { (week, histories) ->
            WeeklyGraphPoint(
                weekLabel = week,
                maxWeight = histories.maxOf { it.weight },
                volume = histories.sumOf {
                    (it.weight * it.reps).toDouble()
                }.toFloat()
            )
        }
        .sortedBy { it.weekLabel }

    val weightData = weeklyGraphData.map {
        it.weekLabel to it.maxWeight
    }

    val volumeData = weeklyGraphData.map {
        it.weekLabel to it.volume
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "성장 그래프",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "5대 운동의 주차별 최고 중량과 총 볼륨 변화를 확인합니다.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (weeklyGraphData.isEmpty()) {
            Text(
                text = "아직 완료된 운동 히스토리가 없습니다.\n루틴을 완료하면 그래프가 표시됩니다.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = "주차별 최고 중량",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            VolumeLineChart(
                data = weightData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "주차별 총 볼륨",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            VolumeLineChart(
                data = volumeData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            weeklyGraphData.forEachIndexed { index, item ->
                Text(
                    text = "${index + 1}. ${item.weekLabel} | 최고 중량 ${item.maxWeight}kg | 총 볼륨 ${item.volume.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun VolumeLineChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val values = data.map { it.second }
        val maxValue = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f

        val graphWidth = size.width
        val graphHeight = size.height

        val leftPadding = 40f
        val bottomPadding = 40f
        val topPadding = 20f
        val rightPadding = 20f

        val usableWidth = graphWidth - leftPadding - rightPadding
        val usableHeight = graphHeight - topPadding - bottomPadding

        drawLine(
            color = gridColor,
            start = Offset(leftPadding, topPadding),
            end = Offset(leftPadding, graphHeight - bottomPadding),
            strokeWidth = 3f
        )

        drawLine(
            color = gridColor,
            start = Offset(leftPadding, graphHeight - bottomPadding),
            end = Offset(graphWidth - rightPadding, graphHeight - bottomPadding),
            strokeWidth = 3f
        )

        val points = data.mapIndexed { index, item ->
            val x = if (data.size == 1) {
                leftPadding + usableWidth / 2
            } else {
                leftPadding + (usableWidth / (data.size - 1)) * index
            }

            val y = graphHeight - bottomPadding -
                    (item.second / maxValue) * usableHeight

            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = primaryColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }

        points.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 9f,
                center = point
            )

            drawCircle(
                color = primaryColor.copy(alpha = 0.25f),
                radius = 18f,
                center = point,
                style = Stroke(width = 4f)
            )
        }
    }
}