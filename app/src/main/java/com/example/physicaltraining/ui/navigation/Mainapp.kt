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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun MainApp(viewModel: WorkoutViewModel) {
    val navController = rememberNavController()


    val timeLeft by viewModel.restTimerLeft.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val showTimeoutDialog by viewModel.showTimeoutDialog.collectAsState()


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
                    viewModel = viewModel
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
                    userName = "홍길동",
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
    }
}


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WorkoutViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("피지컬 트레이닝", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        HomeButton("운동 루틴 목록") { navController.navigate(AppRoute.RoutineList.route) }
        HomeButton("휴식 타이머") { navController.navigate(AppRoute.FreeTimer.route) }
        HomeButton("성장 그래프") { navController.navigate(AppRoute.Graph.route) }
        HomeButton("AI 무게 설정") { navController.navigate(AppRoute.AiSetup.route) }
        HomeButton("Firebase 백업") { viewModel.backupWorkoutHistoryToFirebase()}
    }
}

@Composable
fun HomeButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(56.dp)
    ) {
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