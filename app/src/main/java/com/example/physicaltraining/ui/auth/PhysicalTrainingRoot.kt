package com.example.physicaltraining.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.navigation.MainApp
import com.example.physicaltraining.ui.navigation.AppRoute
import com.example.physicaltraining.ui.screen.LoginScreen
import com.example.physicaltraining.ui.screen.ProfileSetupScreen
import com.example.physicaltraining.ui.theme.PhysicalTrainingTheme

@Composable
fun PhysicalTrainingRoot(
    workoutViewModel: WorkoutViewModel
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val userProfile by workoutViewModel.userProfile.collectAsState()
    val isUserProfileLoaded by workoutViewModel.isUserProfileLoaded.collectAsState()
    var openAiSetupAfterProfile by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(authState.isLoggedIn, authState.uid) {
        if (authState.isLoggedIn) {
            workoutViewModel.setFirebaseBackupUserId(authState.uid)
            workoutViewModel.restoreWorkoutHistoryFromFirebaseOnce()
        } else {
            workoutViewModel.setFirebaseBackupUserId(null)
        }
    }

    LaunchedEffect(userProfile, openAiSetupAfterProfile) {
        if (userProfile != null && openAiSetupAfterProfile) {
            openAiSetupAfterProfile = false
        }
    }

    PhysicalTrainingTheme {
        if (authState.isLoggedIn) {
            when {
                !isUserProfileLoaded -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                userProfile == null -> {
                    ProfileSetupScreen(
                        viewModel = workoutViewModel,
                        onProfileSaved = {
                            openAiSetupAfterProfile = true
                        }
                    )
                }

                else -> {
                    MainApp(
                        viewModel = workoutViewModel,
                        onLogout = {
                            authViewModel.logout()
                        },
                        startDestination = if (openAiSetupAfterProfile) {
                            AppRoute.AiSetup.route
                        } else {
                            AppRoute.Home.route
                        }
                    )
                }
            }
        } else {
            LoginScreen(
                authViewModel = authViewModel
            )
        }
    }
}
