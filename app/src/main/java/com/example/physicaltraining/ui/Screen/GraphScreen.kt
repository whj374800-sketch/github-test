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


@Composable

fun GraphScreen(viewModel: WorkoutViewModel) {
    val routines by viewModel.routines.collectAsState()

    val graphData = routines.map { routine ->
        val totalVolume = routine.exercises.values
            .flatten()
            .filter { it.isChecked }
            .sumOf { (it.weight * it.reps).toDouble() }
            .toFloat()

        routine.name to totalVolume

    }.filter { it.second > 0f }

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
            text = "완료한 세트 기준으로 운동 볼륨(무게 × 횟수)을 계산합니다.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (graphData.isEmpty()) {
            Text(
                text = "아직 완료된 운동 데이터가 없습니다.\n체크리스트에서 세트를 완료하면 그래프가 표시됩니다.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            VolumeLineChart(
                data = graphData,
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

            graphData.forEachIndexed { index, item ->
                Text(
                    text = "${index + 1}. ${item.first} : ${item.second.toInt()} 볼륨",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable

fun VolumeLineChart (
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Canvas(modifier = Modifier) {
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

            val y = graphHeight - bottomPadding - (item.second / maxValue) * usableHeight

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




