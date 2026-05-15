package com.example.physicaltraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physicaltraining.data.WorkoutRepository
import com.example.physicaltraining.data.local.RoutineEntity
import com.example.physicaltraining.data.local.WorkoutSetEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class WorkoutSet(
    val setId: Int = 0,
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
                    "벤치프레스" to listOf(WorkoutSet(weight = 60f, reps =  10),
                                        WorkoutSet(weight = 65f, reps = 8)),
                    "스쿼트" to listOf(WorkoutSet(weight = 80f, reps =  10))
                )
            )
        )
    )
    val routines = _routines.asStateFlow()

    fun addRoutine(name: String) {
        if (name.isBlank()) return
        val newRoutine = DailyRoutine(name = name)

        _routines.update { it + newRoutine }

        viewModelScope.launch {
            repository.insertRoutine(RoutineEntity(id = newRoutine.id, name = newRoutine.name))
        }
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
        var toggleSEtEntity: WorkoutSetEntity? = null


        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()
                    val currentSets = newMap[exercise]?.toMutableList() ?: return@map routine

                    if (setIndex in currentSets.indices) {
                        val updateSet = currentSets[setIndex].copy(
                            isChecked = !currentSets[setIndex].isChecked
                        )
                        currentSets[setIndex] = updateSet
                        newMap[exercise] = currentSets

                        toggleSEtEntity = WorkoutSetEntity(
                            setId = updateSet.setId,
                            routineId = routineId,
                            exerciseName = exercise,
                            weight = updateSet.weight,
                            reps = updateSet.reps,
                            isChecked = updateSet.isChecked
                        )
                    }
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
                toggleSEtEntity?.let { entity ->
        viewModelScope.launch{
            repository.updateSet(entity)
        }
    }
                }

    fun addSet(routineId: String, exercise: String, weight: Float, reps: Int) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()
                    val currentSets = newMap[exercise]?.toMutableList() ?: mutableListOf()

                    val newSet = WorkoutSet(weight = weight, reps = reps, isChecked = false)
                    currentSets.add(newSet)
                    newMap[exercise] = currentSets

                    viewModelScope.launch {
                        repository.insertSets(listOf(
                            WorkoutSetEntity(
                                routineId = routineId,
                                exerciseName = exercise,
                                weight = weight,
                                reps = reps,
                                isChecked = false
                            )
                        ))
                    }
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
    }

    fun removeSet(routineId: String, exercise: String, setIndex: Int) {
        var setIdtoDelete: Int? =null


        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()
                    val currentSets = newMap[exercise]?.toMutableList() ?: return@map routine


                    if (setIndex in currentSets.indices) {
                        setIdtoDelete = currentSets[setIndex].setId
                        currentSets.removeAt(setIndex)
                        newMap[exercise] = currentSets
                    }
                    routine.copy(exercises = newMap)
                } else routine
            }
        }

        setIdtoDelete?.let {setId ->
            viewModelScope.launch {
                repository.deleteSetById(setId)
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