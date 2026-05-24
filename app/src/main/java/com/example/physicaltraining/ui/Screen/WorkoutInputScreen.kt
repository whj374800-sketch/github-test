package com.example.physicaltraining.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physicaltraining.ui.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutInputScreen(
    viewModel: WorkoutViewModel,
    userName: String = "홍길동",
    onCalculationComplete: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var gender by remember { mutableStateOf("남성") }
    var weight by remember { mutableStateOf("") }


    var age by remember { mutableStateOf("") }


    var experience by remember { mutableStateOf("초보자") }
    var expExpanded by remember { mutableStateOf(false) }
    val expOptions = listOf("초보자", "중급자", "상급자")

    var goal by remember { mutableStateOf("근비대") }
    var goalExpanded by remember { mutableStateOf(false) }
    val goalOptions = listOf("근비대", "스트렝스", "다이어트", "유지/재활")


    var selectedRoutineName by remember { mutableStateOf("초보자 전신 루틴") }
    var routineExpanded by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) } // 팝업창 On/Off 상태

    val routineOptions = listOf(
        "nSuns 5/3/1", "PHUL 하이브리드", "StrongLifts 5x5", "PPL 분할 루틴",
        "German Volume (GVT)", "보디빌딩 5일 분할", "파워리프팅 피킹", "초보자 전신 루틴",
        "상하체 분할 파워", "유지 관리 루틴", "무산소 대사 촉진", "5/3/1 BBB"
    )


    LaunchedEffect(experience, goal) {
        selectedRoutineName = when {
            experience == "초보자" && goal == "근비대" -> "보디빌딩 5일 분할"
            experience == "초보자" && goal == "스트렝스" -> "StrongLifts 5x5"
            experience == "초보자" -> "초보자 전신 루틴"
            experience == "중급자" && goal == "근비대" -> "PPL 분할 루틴"
            experience == "중급자" && goal == "스트렝스" -> "nSuns 5/3/1"
            experience == "상급자" && goal == "근비대" -> "German Volume (GVT)"
            experience == "상급자" && goal == "스트렝스" -> "파워리프팅 피킹"
            goal == "다이어트" -> "무산소 대사 촉진"
            goal == "유지/재활" -> "유지 관리 루틴"
            else -> "PHUL 하이브리드"
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("AI 운동 추천 입력", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "${userName} 님,", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "신체 정보와 목표를 입력하시면, AI가 가장 최적화된 주 5일 루틴과 무게를 추천해 드립니다.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }


            Column {
                Text("성별", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (gender == "남성"), onClick = { gender = "남성" })
                    Text("남성")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = (gender == "여성"), onClick = { gender = "여성" })
                    Text("여성")
                }
            }


            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("몸무게 (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("나이 (세)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }


            ExposedDropdownMenuBox(
                expanded = expExpanded,
                onExpandedChange = { expExpanded = !expExpanded }
            ) {
                OutlinedTextField(
                    value = experience,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("운동 경력") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expExpanded, onDismissRequest = { expExpanded = false }) {
                    expOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { experience = option; expExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = goalExpanded,
                onExpandedChange = { goalExpanded = !goalExpanded }
            ) {
                OutlinedTextField(
                    value = goal,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("운동 목적") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = goalExpanded, onDismissRequest = { goalExpanded = false }) {
                    goalOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { goal = option; goalExpanded = false }
                        )
                    }
                }
            }


            Row(verticalAlignment = Alignment.CenterVertically) {
                ExposedDropdownMenuBox(
                    expanded = routineExpanded,
                    onExpandedChange = { routineExpanded = !routineExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedRoutineName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("AI 추천 적용 프로그램") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = routineExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = routineExpanded, onDismissRequest = { routineExpanded = false }) {
                        routineOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { selectedRoutineName = option; routineExpanded = false }
                            )
                        }
                    }
                }


                IconButton(onClick = { showInfoDialog = true }, modifier = Modifier.padding(start = 8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "프로그램 설명 보기",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }


            Button(
                onClick = {
                    val userWeight = weight.toFloatOrNull() ?: 60f
                    val userAge = age.toFloatOrNull() ?: 25f

                    val aiInputData = floatArrayOf(
                        if (gender == "남성") 1f else 0f,
                        userWeight,
                        userAge,
                        if (experience == "상급자") 3f else if (experience == "중급자") 2f else 1f,
                        if (goal == "스트렝스") 1f else 0f
                    )

                    viewModel.applyAiRecommendedRoutine(
                        context = context,
                        userInputs = aiInputData,
                        routineName = selectedRoutineName,
                        onComplete = {
                            onCalculationComplete()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("AI 맞춤 루틴 생성 및 추천받기", fontWeight = FontWeight.Bold)
            }
        }


        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = {
                    Text(text = selectedRoutineName, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(text = getRoutineDescription(selectedRoutineName))
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }
    }
}


fun getRoutineDescription(routineName: String): String {
    return when (routineName) {
        "보디빌딩 5일 분할" -> "초보자와 중급자의 근육 크기(근비대) 성장에 가장 최적화된 루틴입니다. 매일 다른 타겟 부위에 강한 고립 자극을 주어 근육을 조각합니다."
        "초보자 전신 루틴" -> "운동을 처음 시작하는 분들을 위해 복합 다관절 운동 위주로 구성된 기초 체력 및 근신경계 발달 전신 루틴입니다."
        "nSuns 5/3/1" -> "매일 엄청난 세트 수와 고중량을 다루는 강력한 스트렝스 및 볼륨 프로그램입니다. 한계 돌파를 원하는 분들에게 추천합니다."
        "PHUL 하이브리드" -> "근력(Power)을 키우는 날과 근비대(Hypertrophy)를 챙기는 날이 분리되어 있어 두 마리 토끼를 잡을 수 있는 하이브리드 루틴입니다."
        "StrongLifts 5x5" -> "가장 유명하고 검증된 초보자용 스트렝스 루틴입니다. 5회씩 5세트를 수행하며 매일 중량을 증량해 나가는 직관적인 방식입니다."
        "PPL 분할 루틴" -> "밀기(Push), 당기기(Pull), 하체(Legs)로 나누어 부위별 볼륨을 최적화한 현대적인 근비대 밸런스 루틴입니다."
        "German Volume (GVT)" -> "10세트 x 10회라는 극악의 고볼륨으로 근육에 엄청난 펌핑과 자극을 주어 정체기를 박살내는 상급자용 근비대 루틴입니다."
        "파워리프팅 피킹" -> "대회 준비 등 1RM(최대 근력)을 극한으로 끌어올리기 위해 고중량 싱글렛 위주로 구성된 상급자용 스트렝스 프로그램입니다."
        "무산소 대사 촉진" -> "고강도 인터벌 운동이 포함되어 체지방을 연소하면서 동시에 근력을 유지하는 다이어트 및 심폐지구력 최적화 루틴입니다."
        "유지 관리 루틴" -> "부상 위험을 줄이고 가벼운 중량으로 관절을 보호하며 몸의 컨디션을 회복하고 유지하는 재활 특화 루틴입니다."
        else -> "사용자의 신체 능력과 목적에 맞춰 AI가 최적의 무게로 배분한 맞춤형 트레이닝 루틴입니다."
    }
}