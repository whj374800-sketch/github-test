package com.example.physicaltraining.ui

import androidx.lifecycle.ViewModel
import com.example.physicaltraining.data.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class WorkoutSet(
    val weight: Float,
    val reps: Int,
    val isChecked: Boolean = false
)

data class DailyRoutine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val exercises: Map<String, List<WorkoutSet>> = emptyMap()
)

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _routines = MutableStateFlow<List<DailyRoutine>>(
        listOf(
            DailyRoutine(
                name = "기본 샘플 루틴",
                exercises = mapOf(
                    "벤치프레스" to listOf(WorkoutSet(60f, 10), WorkoutSet(65f, 8)),
                    "스쿼트" to listOf(WorkoutSet(80f, 10))
                )
            )
        )
    )
    val routines = _routines.asStateFlow()

    fun addRoutine(name: String) {
        if (name.isBlank()) return
        _routines.update { it + DailyRoutine(name = name) }
    }

    fun addExerciseToRoutine(routineId: String, exerciseName: String) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId && exerciseName.isNotBlank()) {
                    val newMap = routine.exercises.toMutableMap()
                    newMap[exerciseName] = emptyList()
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
    }

    fun toggleSet(routineId: String, exercise: String, setIndex: Int) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()
                    val currentSets = newMap[exercise]?.toMutableList() ?: return@map routine
                    if (setIndex in currentSets.indices) {
                        currentSets[setIndex] = currentSets[setIndex].copy(isChecked = !currentSets[setIndex].isChecked)
                        newMap[exercise] = currentSets
                    }
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
    }

    fun addSet(routineId: String, exercise: String, weight: Float, reps: Int) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()
                    val currentSets = newMap[exercise]?.toMutableList() ?: mutableListOf()
                    currentSets.add(WorkoutSet(weight, reps, isChecked = false))
                    newMap[exercise] = currentSets
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
    }

    fun removeSet(routineId: String, exercise: String, setIndex: Int) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()
                    val currentSets = newMap[exercise]?.toMutableList() ?: return@map routine
                    if (setIndex in currentSets.indices) {
                        currentSets.removeAt(setIndex)
                        newMap[exercise] = currentSets
                    }
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
    }

    fun getAiRestTime(weight: Float, reps: Int): Int {
        return when {
            weight >= 100f -> 100
            weight >= 60f && reps <= 5 -> 120
            else -> 60
        }
    }
}