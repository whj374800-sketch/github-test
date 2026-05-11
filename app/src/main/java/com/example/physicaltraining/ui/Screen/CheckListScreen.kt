package com.example.physicaltraining.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.navigation.AppRoute


@Composable
fun CheckListScreen(viewModel: WorkoutViewModel, navController: NavController) {
    val exerciseSets by viewModel.exerciseSets.collectAsState()

    var showAddDialogFor by remember { mutableStateOf<String?>(null) }
    var inputWeight by remember { mutableStateOf("") }
    var inputReps by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("오늘의 운동 루틴", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(modifier = Modifier.weight(1f)) {
            exerciseSets.forEach { (exercise, sets) ->
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = exercise,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = {
                            navController.navigate(AppRoute.Timer.createRoute(exercise))
                        }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                "타이머 시작",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    sets.forEachIndexed { index, setInfo ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Checkbox(
                                checked = setInfo.isChecked,
                                onCheckedChange = { viewModel.toggleSet(exercise, index) }
                           )
                            Text(
                                text = "${index + 1} 세트 : ${setInfo.weight}kg * ${setInfo.reps}회",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {viewModel.removeSet(exercise, index)}) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "세트 삭제",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = { showAddDialogFor = exercise },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text("+ 세트 추가")
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }
    }

    showAddDialogFor?.let { exerciseName ->
        AlertDialog(
            onDismissRequest = {
                showAddDialogFor = null
            },
            title = { Text("$exerciseName 세트 추가") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputWeight,
                        onValueChange = { inputWeight = it},
                        label = {Text("무게 (kg)")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = inputReps,
                        onValueChange = { inputReps = it},
                        label = {Text("횟수 (회)")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val weight = inputWeight.toFloatOrNull() ?: 20f
                        val reps = inputReps.toIntOrNull() ?: 10

                        viewModel.addSet(exerciseName, weight, reps)

                        inputWeight = ""
                        inputReps = ""
                        showAddDialogFor = null
                    }
                ) {
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = {showAddDialogFor = null}) {
                    Text("취소")
                }
            }
        )
    }


}