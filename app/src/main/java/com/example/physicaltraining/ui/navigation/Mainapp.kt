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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.screen.CheckListScreen

@Composable
fun MainApp(viewModel: WorkoutViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoute.Home.route) {
        composable(AppRoute.Home.route) { HomeScreen(navController) }

        composable(AppRoute.CheckList.route) {
            CheckListScreen(viewModel, navController) }

        composable("timer/{exerciseName}/{initialTime}") { backStackEntry ->
            val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: "운동"
            val initialTime = backStackEntry.arguments?.getString("initialTime")?.toIntOrNull() ?: 60

            TimerScreen(exerciseName, initialTime, navController)

        }
        composable(AppRoute.Graph.route) { GraphScreen() }
        composable(AppRoute.AiSetup.route) { AiSetupScreen() }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("피지컬 트레이닝", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        HomeButton("체크 리스트") {navController.navigate(AppRoute.CheckList.route)}
        HomeButton("휴식 타이머") {navController.navigate("timer/자유 휴식/60")}
        HomeButton("성장 그래프") {navController.navigate(AppRoute.Graph.route)}
        HomeButton("AI 무게 설정") {navController.navigate(AppRoute.AiSetup.route)}
    }
}

@Composable
fun HomeButton(text : String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(56.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}


@Composable
fun TimerScreen(exerciseName: String, initialTime: Int, navController: NavController) {
    var timeLeft by remember { mutableStateOf(initialTime) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = isRunning) {
        while (isRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
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
            Button(onClick = {
                timeLeft = (timeLeft - 10).coerceAtLeast(0)}) {
                Text("-10초")
            }
            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                timeLeft += 10}) {
                Text("+10초")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("휴식 완료")
        }
    }
}
@Composable
fun GraphScreen() { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("성장 그래프 화면") } }
@Composable
fun AiSetupScreen() { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("AI 맞춤 무게 설정 화면") } }