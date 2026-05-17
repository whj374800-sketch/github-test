package com.example.physicaltraining.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.physicaltraining.ui.WorkoutViewModel

data class ExerciseInputData(
    val name: String = "",
    val restTime : String = "60"
)

@Composable
fun RoutineScreen(viewModel: WorkoutViewModel, navController: NavController) {
    val routines by viewModel.routines.collectAsState()

    var showAddRoutineDialog by rememberSaveable { mutableStateOf(false) }
    var inputRoutineName by rememberSaveable { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddRoutineDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "루틴 추가")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("나의 운동 루틴 목록", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(routines) { routine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("checklist/${routine.id}")
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {

                        Box(modifier = Modifier.fillMaxWidth()) {

                            Column(modifier = Modifier.padding(16.dp).padding(end = 48.dp)) {
                                Text(
                                    text = routine.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (routine.exercises.isNotEmpty()) {
                                        "포함된 목록 : ${routine.exercises.keys.joinToString(", ")}"
                                    } else {
                                        "등록된 운동이 없습니다."
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewModel.deleteRoutine(routine.id)
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "루틴 삭제",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showAddRoutineDialog) {
        AddRoutineDialog(
            onDismiss = { showAddRoutineDialog = false },
            onConfirm = { routineName, exerciseDataList ->
                viewModel.addRoutineWithExercises(name = routineName,  exerciseDataList)
                showAddRoutineDialog = false
            }
        )
    }
}

@Composable
fun AddRoutineDialog(
    onDismiss : () -> Unit,
    onConfirm : (String, List<Pair<String,Int>>) -> Unit) {

    var routineName by rememberSaveable { mutableStateOf("") }
    var exercises by rememberSaveable { mutableStateOf(listOf(ExerciseInputData())) }



    AlertDialog (
        onDismissRequest = onDismiss,
        title = { Text("새로운 루틴 만들기")},
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("루틴 이름 (예 가슴/삼두)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("운동 추가 (이름 및 휴식시간)", style = MaterialTheme.typography.titleSmall)

                exercises.forEachIndexed { index, exercise ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedTextField(
                            value = exercise.name,
                            onValueChange = { newValue ->
                                val newList = exercises.toMutableList()
                                newList[index] = exercise.copy(name = newValue)
                                exercises = newList
                            },
                            label = { Text("운동 ${index + 1} ") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = exercise.restTime,
                            onValueChange = { newValue ->
                                val newList = exercises.toMutableList()
                                newList[index] = exercise.copy(restTime = newValue)
                                exercises = newList
                            },
                            label = { Text("휴식(초)") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                        )
                    }
                }
                TextButton(
                    onClick = { exercises = exercises + ExerciseInputData() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("+ 운동 칸 추가하기")
                }
            }
        },
            confirmButton = {
                TextButton(onClick = {

                    val exerciseDataList = exercises.map {
                        Pair(it.name, it.restTime.toIntOrNull() ?: 60)
                    }
                    onConfirm(routineName, exerciseDataList)
                }) { Text("만들기") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("취소") }
            }
            )
        }



