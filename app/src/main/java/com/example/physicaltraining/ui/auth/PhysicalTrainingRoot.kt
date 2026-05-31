package com.example.physicaltraining.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.physicaltraining.ui.Screen.LoginScreen
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.navigation.MainApp

@Composable
fun PhysicalTrainingRoot(
    workoutViewModel: WorkoutViewModel
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    if (authState.isLoggedIn) {
        MainApp(
            viewModel = workoutViewModel,
            onLogout = {
                authViewModel.logout()
            }
        )
    } else {
        LoginScreen(
            authViewModel = authViewModel
        )
    }
}