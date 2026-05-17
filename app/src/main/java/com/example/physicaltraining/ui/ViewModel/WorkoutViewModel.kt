package com.example.physicaltraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physicaltraining.data.WorkoutRepository
import com.example.physicaltraining.data.local.RoutineEntity
import com.example.physicaltraining.data.local.WorkoutSetEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val exercises: Map<String, List<WorkoutSet>> = emptyMap(),
    val restTime: Int= 60
)

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _routines = MutableStateFlow<List<DailyRoutine>>(emptyList())
    val routines = _routines.asStateFlow()

    init {
        loadRoutinesFromDb()
    }

    private fun loadRoutinesFromDb() {
        viewModelScope.launch {
            repository.allRoutines.collect { routineEntities ->
                val loadedRoutines = routineEntities.map { entity ->
                    val setEntities = repository.getSetsForRoutine(entity.id).first()

                    val exerciseMap = setEntities.groupBy { it.exerciseName }
                        .mapValues { (_, sets) ->
                            sets.map { setEntity ->
                                WorkoutSet(
                                    setId = setEntity.setId,
                                    weight = setEntity.weight,
                                    reps = setEntity.reps,
                                    isChecked = setEntity.isChecked
                                )
                            }
                        }

                    DailyRoutine(
                        id = entity.id,
                        name = entity.name,
                        exercises = exerciseMap,
                        restTime = entity.restTime

                    )
                }
                _routines.value = loadedRoutines

            }
        }
    }
    fun addRoutine(name: String) {
        if (name.isBlank()) return
        val newRoutine = DailyRoutine(name = name)

        viewModelScope.launch {
            repository.insertRoutine(RoutineEntity(id = newRoutine.id, name = newRoutine.name))
        }
    }

    fun addExerciseToRoutine(routineId: String, exerciseName: String) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId && exerciseName.isNotBlank()) {
                    val newMap = routine.exercises.toMutableMap()
                    if (!newMap.containsKey(exerciseName)) {
                        newMap[exerciseName] = emptyList()
                    }
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

    fun addRoutineWithExercises(name: String, exerciseName: List<String>, restTime: Int) {
        if (name.isBlank()) return

        val newRoutineId = UUID.randomUUID().toString()
        val newRoutineEntity = RoutineEntity(id = newRoutineId, name = name, restTime = restTime)

        val initialSets = exerciseName
            .filter { it.isNotBlank() }
            .map { exerciseName->
                WorkoutSetEntity(
                    routineId = newRoutineId,
                    exerciseName = exerciseName,
                    weight = 0f,
                    reps = 0,
                    isChecked = false
                )
            }

        viewModelScope.launch {
            repository.insertRoutine(newRoutineEntity)
            if (initialSets.isNotEmpty()) {
                repository.insertSets(initialSets)
            }
        }

    }
}