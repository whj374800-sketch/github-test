package com.example.physicaltraining.ui.screen

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
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

    var selectedExercise by remember { mutableStateOf("벤치프레스") }
    var expanded by remember { mutableStateOf(false) }

    val filteredHistory = workoutHistory.filter { history ->
        history.exerciseName.contains(selectedExercise)
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
    val bestWeight = weeklyGraphData.maxOfOrNull { it.maxWeight } ?: 0f
    val latestWeight = weeklyGraphData.lastOrNull()?.maxWeight ?: 0f
    val totalVolume = weeklyGraphData.sumOf { it.volume.toDouble() }.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "성장 그래프",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "주차별 최고 중량과 총 볼륨 변화를 확인합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedExercise,
                onValueChange = {},
                readOnly = true,
                label = { Text("운동 선택") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                targetExercises.forEach { exercise ->
                    DropdownMenuItem(
                        text = { Text(exercise) },
                        onClick = {
                            selectedExercise = exercise
                            expanded = false
                        }
                    )
                }
            }
        }

        if (weeklyGraphData.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "아직 완료된 운동 히스토리가 없습니다.\n루틴을 완료하면 그래프가 표시됩니다.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(18.dp)
                )
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GraphMetricCard(
                    label = "최고 중량",
                    value = "${formatGraphValue(bestWeight)}kg",
                    modifier = Modifier.weight(1f)
                )
                GraphMetricCard(
                    label = "최근 기록",
                    value = "${formatGraphValue(latestWeight)}kg",
                    modifier = Modifier.weight(1f)
                )
                GraphMetricCard(
                    label = "총 볼륨",
                    value = totalVolume.toInt().toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            ChartCard(title = "주차별 최고 중량") {
                VolumeLineChart(
                    data = weightData,
                    suffix = "kg",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                )
            }

            ChartCard(title = "주차별 총 볼륨") {
                VolumeLineChart(
                    data = volumeData,
                    suffix = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "주차별 기록",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    weeklyGraphData.forEachIndexed { index, item ->
                        Text(
                            text = "${index + 1}. ${item.weekLabel}  최고 ${formatGraphValue(item.maxWeight)}kg  볼륨 ${item.volume.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GraphMetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
fun VolumeLineChart(
    data: List<Pair<String, Float>>,
    suffix: String = "",
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

        val leftPadding = 48f
        val bottomPadding = 48f
        val topPadding = 32f
        val rightPadding = 24f

        val usableWidth = graphWidth - leftPadding - rightPadding
        val usableHeight = graphHeight - topPadding - bottomPadding

        val labelPaint = Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 26f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val axisPaint = Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 22f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

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

        drawContext.canvas.nativeCanvas.drawText(
            maxValue.toInt().toString(),
            leftPadding - 8f,
            topPadding + 8f,
            axisPaint
        )

        drawContext.canvas.nativeCanvas.drawText(
            "0",
            leftPadding - 8f,
            graphHeight - bottomPadding,
            axisPaint
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

        points.forEachIndexed { index, point ->
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

            drawContext.canvas.nativeCanvas.drawText(
                "${formatGraphValue(data[index].second)}$suffix",
                point.x,
                point.y - 16f,
                labelPaint
            )

            drawContext.canvas.nativeCanvas.drawText(
                data[index].first,
                point.x,
                graphHeight - 8f,
                labelPaint
            )
        }
    }
}

private fun formatGraphValue(value: Float): String {
    return if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        String.format(Locale.getDefault(), "%.1f", value)
    }
}
