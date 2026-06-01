package com.example.physicaltraining.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.navigation.AppRoute

private fun isBodyWeightOnlyExercise(exerciseName: String): Boolean {
    return exerciseName == "풀업" || exerciseName == "딥스"
}

@Composable
fun CheckListScreen(routineId: String, viewModel: WorkoutViewModel, navController: NavController) {
    val routines by viewModel.routines.collectAsState()
    val currentRoutine = routines.find { it.id == routineId }

    if (currentRoutine == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showSetDialogFor by remember { mutableStateOf<String?>(null) }
    var inputWeight by remember { mutableStateOf("") }
    var inputReps by remember { mutableStateOf("") }
    val totalSets = currentRoutine.exercises.values.sumOf { it.size }
    val completedSets = currentRoutine.exercises.values.flatten().count { it.isChecked }
    val progress = if (totalSets == 0) 0f else completedSets / totalSets.toFloat()


    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = { showAddExerciseDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("운동 종목 추가", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = currentRoutine.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completedSets/$totalSets 세트 완료",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentRoutine.exercises.entries.toList()) { (exercise, sets) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${sets.size}세트 · 휴식 ${sets.firstOrNull()?.restTime ?: 60}초",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                val currentRestTime = sets.firstOrNull()?.restTime ?: 60
                                navController.navigate(AppRoute.Timer.createRoute(routineId = routineId, exerciseName = exercise, initialTime = currentRestTime ))
                            }) {
                                Icon(Icons.Default.PlayArrow, "타이머 시작", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        HorizontalDivider()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SET", modifier = Modifier.width(52.dp), style = MaterialTheme.typography.labelMedium)
                            Text("KG", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                            Text("REPS", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                            Text("완료", modifier = Modifier.width(72.dp), style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.width(48.dp))
                        }

                        sets.forEachIndexed { index, setInfo ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    modifier = Modifier.width(52.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBodyWeightOnlyExercise(exercise)) "-" else "${setInfo.weight}",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${setInfo.reps}",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Checkbox(
                                    checked = setInfo.isChecked,
                                    onCheckedChange = { isChecked ->
                                        viewModel.toggleSet(currentRoutine.id, exercise, index)
                                        if (isChecked) {
                                            navController.navigate(
                                                AppRoute.Timer.createRoute(
                                                    routineId = routineId,
                                                    exerciseName = exercise,
                                                    initialTime = setInfo.restTime
                                                )
                                            )
                                        }
                                    },
                                    modifier = Modifier.width(72.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.removeSet(currentRoutine.id, exercise, index) },
                                    modifier = Modifier.width(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        "세트 삭제",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        TextButton(
                            onClick = { showSetDialogFor = exercise },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("세트 추가")
                        }
                        }
                    }
                }
            }
        }
    }

    if (showAddExerciseDialog) {
        AddExerciseFromCatalogDialog(
            onDismiss = { showAddExerciseDialog = false },
            onConfirm = { exerciseName, restTime ->
                viewModel.addExerciseToRoutine(
                    routineId = currentRoutine.id,
                    exerciseName = exerciseName,
                    restTime = restTime
                )
                showAddExerciseDialog = false
            }
        )
    }

    showSetDialogFor?.let { exerciseName ->
        AlertDialog(
            onDismissRequest = { showSetDialogFor = null },
            title = { Text("$exerciseName 세트 추가") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputWeight,
                        onValueChange = { inputWeight = it },
                        label = { Text("무게 (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputReps,
                        onValueChange = { inputReps = it },
                        label = { Text("횟수 (회)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val weight = inputWeight.toFloatOrNull() ?: 20f
                    val reps = inputReps.toIntOrNull() ?: 10
                    viewModel.addSet(currentRoutine.id, exerciseName, weight, reps)
                    inputWeight = ""
                    inputReps = ""
                    showSetDialogFor = null
                }) { Text("추가") }
            },
            dismissButton = {
                TextButton(onClick = { showSetDialogFor = null }) { Text("취소") }
            }
        )
    }
}

@Composable
fun AddExerciseFromCatalogDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    val context = LocalContext.current
    val catalog = remember { loadRoutineExerciseCatalog(context) }
    var selectedExercise by remember { mutableStateOf(catalog.firstOrNull()) }
    var restTime by remember { mutableStateOf("60") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("운동 종목 추가") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                selectedExercise?.let { selected ->
                    ExercisePreviewCard(item = selected)
                }

                OutlinedTextField(
                    value = restTime,
                    onValueChange = { restTime = it },
                    label = { Text("휴식 시간 (초)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(catalog) { item ->
                        val isSelected = item.name == selectedExercise?.name
                        ExerciseCatalogRow(
                            item = item,
                            selected = isSelected,
                            onClick = { selectedExercise = item }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedExercise?.let {
                        onConfirm(it.name, restTime.toIntOrNull() ?: 60)
                    }
                }
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun ExercisePreviewCard(item: RoutineExerciseCatalogItem) {
    val context = LocalContext.current
    val imageBitmap = remember(item.imageAsset) {
        item.imageAsset?.let { loadAssetImageBitmap(context, it) }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "등록된 운동 이미지가 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExerciseCatalogRow(
    item: RoutineExerciseCatalogItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (item.matched) "이미지 매칭됨" else "이미지 없음",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
