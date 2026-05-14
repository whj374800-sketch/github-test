package com.example.physicaltraining.ui

import androidx.lifecycle.ViewModel
import com.example.physicaltraining.data.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class WorkoutSet(
    val weight : Float,
    val reps : Int,
    val isChecked : Boolean = false
)

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel()
{

    private val _exerciseSets = MutableStateFlow<Map<String, List<WorkoutSet>>>(
    mapOf(
        "벤치프레스" to listOf(WorkoutSet(60f, 10), WorkoutSet(65f, 8), WorkoutSet(65f, 8)),
        "스쿼트" to listOf(WorkoutSet(80f, 10), WorkoutSet(85f, 8), WorkoutSet(85f, 8)),
        "데드리프트" to listOf(WorkoutSet(90f, 5), WorkoutSet(100f, 5), WorkoutSet(100f, 5))
        )
    )
    val exerciseSets = _exerciseSets.asStateFlow()

    fun toggleSet(exercise: String,setIndex: Int)
    {
    _exerciseSets.update { currentMap ->
        val currentSets = currentMap[exercise]?.toMutableList() ?: return@update currentMap

        if (setIndex in currentSets.indices) {
            currentSets[setIndex] = currentSets[setIndex].copy(
                isChecked = !currentSets[setIndex].isChecked
            )
        }
    currentMap + (exercise to currentSets)

        }
    }


fun addSet(exercise: String, weight: Float, reps: Int) {
    _exerciseSets.update { currentMap ->
        val currentSets = currentMap[exercise]?.toMutableList() ?: return@update currentMap

        currentSets.add(WorkoutSet(weight, reps, isChecked = false))

        currentMap + (exercise to currentSets)
    }
}

fun removeSet(exercise: String, setIndex: Int) {
    _exerciseSets.update { currentMap ->
        val currentSets = currentMap[exercise]?.toMutableList() ?: return@update currentMap

        if (setIndex in currentSets.indices) {
            currentSets.removeAt(setIndex)
        }

        currentMap + (exercise to currentSets)
    }
}

    fun getAiRestTime(weight: Float, reps: Int) : Int {
        return when {
            weight >= 100f -> 100
            weight >= 60f && reps <= 5 -> 120
            else -> 60
        }
    }

    fun addExercise(exerciseName: String) {
        _exerciseSets.update { currentMap ->
            if (currentMap.containsKey(exerciseName) || exerciseName.isBlank())
            {
                currentMap
            }
            else {
                currentMap + (exerciseName to emptyList())
            }
        }
    }


}