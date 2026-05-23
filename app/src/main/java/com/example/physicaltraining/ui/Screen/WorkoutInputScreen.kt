package com.example.physicaltraining.ui.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.physicaltraining.ui.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutInputScreen(viewModel: WorkoutViewModel) {

    val scrollState = rememberScrollState()

    var gender by remember { mutableStateOf("남성") }
    var weight by remember { mutableStateOf("")}
    var ageGroup by remember { mutableStateOf("20대") }
    var experience by remember { mutableStateOf("초보자") }
    var goal by remember { mutableStateOf("근비대") }
    var selectedExercise by remember { mutableStateOf("벤치프레스") }
    var expanded by remember { mutableStateOf(false) }

    val exercise = listOf(
        "오버헤드 프레스", "사이드 레터럴 레이즈", "숄더 프레스 머신", "페이스 풀",
        "바벨 스쿼트", "루마니안 데드리프트", "레그 프레스 머신", "레그 익스텐션 머신", "레그 컬 머신", "워킹 런지",
        "벤치프레스", "인클라인 덤벨 프레스", "체스트 프레스 머신", "펙덱 플라이 머신",
        "바벨 로우", "랫 풀다운 머신", "시티드 로우 머신", "어시스트 풀업 머신"
    )

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(" AI 운동 추천 입력", fontWeight = FontWeight.Bold)}) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("성별", fontWeight = FontWeight.Bold)
            Row {
                RadioButton(selected = (gender == "남성"), onClick = {gender = "남성"})
                Text("남성", modifier = Modifier.padding(top = 12.dp))
                RadioButton(selected = (gender == "여성"), onClick = {gender = "여성"})
                Text("여성", modifier = Modifier.padding(top = 12.dp))
            }

            OutlinedTextField(
                value = weight,
                onValueChange = {weight = it},
                label = {Text("현재 몸무게 (kg)")},
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded}
            ) {
                OutlinedTextField(
                    value = selectedExercise,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("운동 종목")},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false}) {
                    exercise.forEach { ex ->
                        DropdownMenuItem(text = { Text(ex)}, onClick = { selectedExercise = ex; expanded = false})

                    }
                }
            }
            Button(
                onClick = {

                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("운동 추천받기")
            }
        }
    }
}