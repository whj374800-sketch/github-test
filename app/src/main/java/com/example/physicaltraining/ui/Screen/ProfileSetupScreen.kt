package com.example.physicaltraining.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.physicaltraining.ui.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: WorkoutViewModel
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("남성") }
    var experience by remember { mutableStateOf("초보자") }
    var goal by remember { mutableStateOf("근비대") }
    var experienceExpanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val experienceOptions = listOf("초보자", "중급자", "상급자")
    val goalOptions = listOf("근비대", "스트렝스", "다이어트", "유지/재활")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("기본 정보 입력", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "맞춤 루틴을 만들기 위한 기본 정보를 입력해주세요.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("이름") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("성별", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(selected = gender == "남성", onClick = { gender = "남성" })
                Text("남성")
                RadioButton(selected = gender == "여성", onClick = { gender = "여성" })
                Text("여성")
            }

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("나이") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("몸무게 (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = experienceExpanded,
                onExpandedChange = { experienceExpanded = !experienceExpanded }
            ) {
                OutlinedTextField(
                    value = experience,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("운동 경력") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = experienceExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = experienceExpanded,
                    onDismissRequest = { experienceExpanded = false }
                ) {
                    experienceOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                experience = option
                                experienceExpanded = false
                            }
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
                    label = { Text("운동 목표") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = goalExpanded,
                    onDismissRequest = { goalExpanded = false }
                ) {
                    goalOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                goal = option
                                goalExpanded = false
                            }
                        )
                    }
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    val parsedAge = age.toIntOrNull()
                    val parsedWeight = weight.toFloatOrNull()

                    when {
                        name.isBlank() -> errorMessage = "이름을 입력해주세요."
                        parsedAge == null || parsedAge <= 0 -> errorMessage = "나이를 올바르게 입력해주세요."
                        parsedWeight == null || parsedWeight <= 0f -> errorMessage = "몸무게를 올바르게 입력해주세요."
                        else -> {
                            errorMessage = null
                            viewModel.saveUserProfile(
                                name = name.trim(),
                                age = parsedAge,
                                weight = parsedWeight,
                                gender = gender,
                                experience = experience,
                                goal = goal
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("저장하고 시작하기", fontWeight = FontWeight.Bold)
            }
        }
    }
}
