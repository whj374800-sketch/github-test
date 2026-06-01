package com.example.physicaltraining.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.screen.CheckListScreen
import com.example.physicaltraining.ui.screen.GraphScreen
import com.example.physicaltraining.ui.screen.RestTimeBar
import com.example.physicaltraining.ui.screen.RoutineScreen
import com.example.physicaltraining.ui.screen.TimeoutDialog
import com.example.physicaltraining.ui.screen.WorkoutInputScreen


@Composable
fun MainApp(
    viewModel: WorkoutViewModel,
    onLogout : () -> Unit = {}
) {
    val navController = rememberNavController()


    val timeLeft by viewModel.restTimerLeft.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val showTimeoutDialog by viewModel.showTimeoutDialog.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val weeklyProgressionRequest by viewModel.weeklyProgressionRequest.collectAsState()


    Box(modifier = Modifier.fillMaxSize()) {


        NavHost(
            navController = navController,
            startDestination = AppRoute.Home.route,
            modifier = Modifier.systemBarsPadding()
        ) {

            composable(AppRoute.FreeTimer.route) {
                FreeTimerScreen(navController = navController)
            }

            composable(AppRoute.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = viewModel,
                    onLogout = onLogout
                )
            }


            composable(AppRoute.RoutineList.route) {
                RoutineScreen(viewModel = viewModel, navController = navController)
            }


            composable(
                route = AppRoute.CheckList.route,
                arguments = listOf(navArgument("routineId") { type = NavType.StringType })
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
                CheckListScreen(routineId = routineId, viewModel = viewModel, navController = navController)
            }


            composable(
                route = AppRoute.Timer.route,
                arguments = listOf(
                    navArgument("routineId") {type =  NavType.StringType },
                    navArgument("exerciseName") { type = NavType.StringType },
                    navArgument("initialTime") { type = NavType.StringType }

                )
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
                val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: "운동"
                val initialTime = backStackEntry.arguments?.getString("initialTime")?.toIntOrNull() ?: 60
                TimerScreen(
                    routineId = routineId,
                    exerciseName = exerciseName,
                    initialTime = initialTime,
                    navController = navController,
                    viewModel = viewModel)
            }

            composable(AppRoute.Graph.route) {
                GraphScreen(viewModel = viewModel) }



            composable(AppRoute.AiSetup.route) {
                WorkoutInputScreen(
                    viewModel = viewModel,
                    userName = userProfile?.name ?: "사용자",
                    onCalculationComplete = {

                        navController.navigate(AppRoute.RoutineList.route) {

                            popUpTo(AppRoute.Home.route)
                        }
                    }
                )
            }
        }


        Column(modifier = Modifier.fillMaxWidth()) {
            RestTimeBar(
                timeLeft = timeLeft,
                isRunning = isTimerRunning,
                onSkip = { viewModel.stopRestTimer() }
            )
        }


        if (showTimeoutDialog) {
            TimeoutDialog (
                onDismiss = { viewModel.dismissTimeoutDialog() }
            )
        }

        weeklyProgressionRequest?.let { request ->
            AlertDialog(
                onDismissRequest = { viewModel.cancelWeeklyProgression() },
                title = { Text("주간 루틴 완료") },
                text = {
                    Text(
                        "${request.programName}의 이번 주 루틴 ${request.routineCount}개, " +
                                "총 ${request.setCount}세트를 모두 완료했습니다.\n\n" +
                                "다음 주 운동을 위해 기존 루틴의 무게를 증량하고 완료 체크를 초기화해도 괜찮을까요?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmWeeklyProgression() }) {
                        Text("증량하기")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cancelWeeklyProgression() }) {
                        Text("나중에")
                    }
                }
            )
        }
    }
}


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel : WorkoutViewModel,
    onLogout: () -> Unit = {}

) {
    val routines by viewModel.routines.collectAsState()
    val history by viewModel.workoutHistory.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val nextRoutine = routines.firstOrNull { routine ->
        routine.exercises.values.flatten().any { !it.isChecked }
    } ?: routines.firstOrNull()
    val totalSets = routines.sumOf { routine -> routine.exercises.values.sumOf { it.size } }
    val completedSets = routines.sumOf { routine -> routine.exercises.values.flatten().count { it.isChecked } }
    val progress = if (totalSets == 0) 0f else completedSets / totalSets.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "${profile?.name?.takeIf { it.isNotBlank() } ?: "사용자"} 님",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "오늘의 트레이닝",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = nextRoutine?.name ?: "아직 등록된 루틴이 없습니다",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (nextRoutine == null) {
                        "AI 무게 설정에서 첫 루틴을 생성해보세요."
                    } else {
                        "${completedSets}/${totalSets} 세트 완료"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = {
                        nextRoutine?.let {
                            navController.navigate(AppRoute.CheckList.createRoute(it.id))
                        } ?: navController.navigate(AppRoute.AiSetup.route)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (nextRoutine == null) "루틴 만들기" else "운동 시작")
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeMetric("루틴", routines.size.toString(), Modifier.weight(1f))
            HomeMetric("운동", routines.sumOf { it.exercises.size }.toString(), Modifier.weight(1f))
            HomeMetric("기록", history.size.toString(), Modifier.weight(1f))
        }

        Text("빠른 메뉴", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        HomeActionButton(Icons.Default.PlayArrow, "운동 루틴 목록") {
            navController.navigate(AppRoute.RoutineList.route)
        }
        HomeActionButton(Icons.Default.PlayArrow, "휴식 타이머") {
            navController.navigate(AppRoute.FreeTimer.route)
        }
        HomeActionButton(Icons.Default.PlayArrow, "성장 그래프") {
            navController.navigate(AppRoute.Graph.route)
        }
        HomeActionButton(Icons.Default.Add, "AI 맞춤 루틴 생성") {
            navController.navigate(AppRoute.AiSetup.route)
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("로그아웃")
        }
    }
}

@Composable
fun HomeMetric(label: String, value: String, modifier: Modifier = Modifier) {
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
fun HomeActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun TimerScreen(
    routineId : String,
    exerciseName: String,
    initialTime: Int,
    navController: NavController,
    viewModel: WorkoutViewModel
) {
    val timeLeft by viewModel.restTimerLeft.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()

    LaunchedEffect(Unit) {
        if (!isRunning && timeLeft <= 0) {
            viewModel.startRestTimer(initialTime)
        }
    }

    LaunchedEffect(isRunning, timeLeft) {
        if (!isRunning && timeLeft <= 0) {
            if (routineId != "free" ) {
                navController.navigate(AppRoute.CheckList.createRoute(routineId)) {
                    popUpTo(AppRoute.RoutineList.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            } else {
                navController.popBackStack()
            }



        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = exerciseName, style = MaterialTheme.typography.headlineMedium)
        Text(text = "휴식 중", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(
                onClick = {
                    viewModel.adjustRestTimer(-10)
                }
            ) {
                Text("-10초")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    viewModel.adjustRestTimer(10)
                }
            ) {
                Text("+10초")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                viewModel.stopRestTimer()

                if (routineId != "free") {
                    navController.navigate(AppRoute.CheckList.createRoute(routineId)) {
                        popUpTo(AppRoute.RoutineList.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }else {
                    navController.popBackStack()
                }
            }
        ) {
            Text("휴식 완료")
        }
    }
}
@Composable
fun FreeTimerScreen(navController: NavController) {
    var timeLeft by remember { mutableStateOf(60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        }

        if (timeLeft <= 0) {
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "자유 휴식 타이머",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(
                onClick = {
                    timeLeft = (timeLeft - 10).coerceAtLeast(0)
                }
            ) {
                Text("-10초")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    timeLeft += 10
                }
            ) {
                Text("+10초")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(
                onClick = {
                    isRunning = !isRunning
                }
            ) {
                Text(if (isRunning) "일시정지" else "시작")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    timeLeft = 60
                    isRunning = false
                }
            ) {
                Text("초기화")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("돌아가기")
        }
    }
}
