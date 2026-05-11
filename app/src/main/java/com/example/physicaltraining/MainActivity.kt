package com.example.physicaltraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.physicaltraining.data.WorkoutRepository
import com.example.physicaltraining.data.local.AppDatabase
import com.example.physicaltraining.ui.navigation.MainApp
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.theme.PhysicalTrainingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val repository = WorkoutRepository(database.workoutDao())

        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkoutViewModel(repository) as T
            }
        })[WorkoutViewModel::class.java]

        setContent {
            PhysicalTrainingTheme {
                MainApp(viewModel)
            }
        }
    }
}