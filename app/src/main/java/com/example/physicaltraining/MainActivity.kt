package com.example.physicaltraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.physicaltraining.data.WorkoutRepository
import com.example.physicaltraining.data.local.AppDatabase
import com.example.physicaltraining.data.remote.FirebaseWorkoutRepository
import com.example.physicaltraining.ui.WorkoutViewModel
import com.example.physicaltraining.ui.auth.PhysicalTrainingRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "workout_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val firebaseWorkoutRepository = FirebaseWorkoutRepository()

        val repository = WorkoutRepository(
            workoutDao = database.workoutDao(),
            firebaseWorkoutRepository = firebaseWorkoutRepository
        )

        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return WorkoutViewModel(repository) as T
                }
                throw IllegalArgumentException("알 수 없는 ViewModel 클래스입니다.")
            }
        }

        val viewModel = ViewModelProvider(this, viewModelFactory)[WorkoutViewModel::class.java]

        setContent {
            PhysicalTrainingRoot(
                workoutViewModel = viewModel
            )
        }
    }
}

