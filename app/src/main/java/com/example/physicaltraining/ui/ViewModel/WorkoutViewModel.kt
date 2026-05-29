package com.example.physicaltraining.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physicaltraining.Ai.WorkoutModelManager
import com.example.physicaltraining.Domain.CalculatedExercise
import com.example.physicaltraining.Domain.ProgressionManager
import com.example.physicaltraining.Domain.RoutineWeightCalculator
import com.example.physicaltraining.data.WorkoutRepository
import com.example.physicaltraining.data.local.RoutineEntity
import com.example.physicaltraining.data.local.UserProfileEntity
import com.example.physicaltraining.data.local.WorkoutHistoryEntity
import com.example.physicaltraining.data.local.WorkoutSetEntity
import com.example.physicaltraining.data.template.RoutineRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val isChecked: Boolean = false,
    val restTime: Int = 60
)

data class DailyRoutine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val exercises: Map<String, List<WorkoutSet>> = emptyMap()
)

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _workoutHistory =
        MutableStateFlow<List<WorkoutHistoryEntity>>(emptyList())

    val workoutHistory = _workoutHistory.asStateFlow()

    private var modelManager: WorkoutModelManager? = null

    private var timerJob: Job? = null

    private val _restTimeLeft = MutableStateFlow(0)
    val restTimerLeft = _restTimeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _showTimeoutDialog = MutableStateFlow(false)
    val showTimeoutDialog = _showTimeoutDialog.asStateFlow()


    private val _routines = MutableStateFlow<List<DailyRoutine>>(emptyList())
    val routines = _routines.asStateFlow()

    private val _userProfile =
        MutableStateFlow<UserProfileEntity?>(null)

    val userProfile = _userProfile.asStateFlow()

    init {
        loadRoutinesFromDb()
        loadUserProfile()
        loadWorkoutHistory()
    }

    fun initAiModel(context: android.content.Context) {
        if (modelManager == null) {
            modelManager = WorkoutModelManager(context)
        }
    }

    fun getAiRecommendation(inputData: FloatArray): FloatArray {
        return modelManager?.predict(inputData) ?: floatArrayOf(0f, 0f, 0f, 0f)
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
                                    isChecked = setEntity.isChecked,
                                    restTime = setEntity.restTime
                                )
                            }
                        }

                    DailyRoutine(
                        id = entity.id,
                        name = entity.name,
                        exercises = exerciseMap

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

    fun addExerciseToRoutine(routineId: String, exerciseName: String, restTime: Int = 60) {
        if (exerciseName.isBlank()) return

        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()

                    if (!newMap.containsKey(exerciseName)) {
                        val initialSet = WorkoutSet(
                            weight = 0f,
                            reps = 0,
                            isChecked = false,
                            restTime = restTime
                        )
                        newMap[exerciseName] = listOf(initialSet)

                        viewModelScope.launch {
                            repository.insertSets(
                                listOf(
                                    WorkoutSetEntity(
                                        routineId = routineId,
                                        exerciseName = exerciseName,
                                        weight = 0f,
                                        reps = 0,
                                        isChecked = false,
                                        restTime = restTime
                                    )
                                )
                            )
                        }

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

                        if (updateSet.isChecked) {
                            startRestTimer(updateSet.restTime)
                        }

                        toggleSEtEntity = WorkoutSetEntity(
                            setId = updateSet.setId,
                            routineId = routineId,
                            exerciseName = exercise,
                            weight = updateSet.weight,
                            reps = updateSet.reps,
                            isChecked = updateSet.isChecked,
                            restTime = updateSet.restTime
                        )
                    }
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
        toggleSEtEntity?.let { entity ->
            viewModelScope.launch {
                repository.updateSet(entity)

                if (entity.isChecked) {
                    checkAndCreateNextWeekRoutine(routineId)
                }
            }
        }
    }

    fun addSet(routineId: String, exercise: String, weight: Float, reps: Int) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newMap = routine.exercises.toMutableMap()
                    val currentSets = newMap[exercise]?.toMutableList() ?: mutableListOf()

                    val existingRestTime = currentSets.firstOrNull()?.restTime ?: 60

                    val newSet = WorkoutSet(weight = weight, reps = reps, isChecked = false)
                    currentSets.add(newSet)
                    newMap[exercise] = currentSets

                    viewModelScope.launch {
                        repository.insertSets(
                            listOf(
                                WorkoutSetEntity(
                                    routineId = routineId,
                                    exerciseName = exercise,
                                    weight = weight,
                                    reps = reps,
                                    isChecked = false,
                                    restTime = existingRestTime
                                )
                            )
                        )
                    }
                    routine.copy(exercises = newMap)
                } else routine
            }
        }
    }

    fun removeSet(routineId: String, exercise: String, setIndex: Int) {
        var setIdtoDelete: Int? = null


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

        setIdtoDelete?.let { setId ->
            viewModelScope.launch {
                repository.deleteSetById(setId)
            }
        }
    }


    fun addRoutineWithExercises(name: String, exerciseName: List<Pair<String, Int>>) {
        if (name.isBlank()) return

        val newRoutineId = UUID.randomUUID().toString()
        val newRoutineEntity = RoutineEntity(id = newRoutineId, name = name)

        val initialSets = exerciseName
            .filter { it.first.isNotBlank() }
            .map { (exerciseName, exerciseRest) ->
                WorkoutSetEntity(
                    routineId = newRoutineId,
                    exerciseName = exerciseName,
                    weight = 0f,
                    reps = 0,
                    isChecked = false,
                    restTime = exerciseRest
                )
            }

        viewModelScope.launch {
            repository.insertRoutine(newRoutineEntity)
            if (initialSets.isNotEmpty()) {
                repository.insertSets(initialSets)
            }
        }

    }

    fun deleteRoutine(routineId: String) {
        _routines.update { currentList ->
            currentList.filter { it.id != routineId }
        }

        viewModelScope.launch {
            repository.deleteRoutine(RoutineEntity(id = routineId, name = ""))
        }
    }

    fun deleteExercise(routineId: String, exerciseName: String) {
        _routines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    val newExercise = routine.exercises.toMutableMap()
                    newExercise.remove(exerciseName)
                    routine.copy(exercises = newExercise)
                } else routine
            }
        }
    }

    fun startRestTimer(restTime: Int) {
        timerJob?.cancel()

        _showTimeoutDialog.value = false
        _restTimeLeft.value = restTime
        _isTimerRunning.value = true

        timerJob = viewModelScope.launch {
            while (_restTimeLeft.value > 0) {
                delay(1000L)
                _restTimeLeft.value -= 1
            }
            _isTimerRunning.value = false
            _showTimeoutDialog.value = true
        }
    }

    fun stopRestTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _restTimeLeft.value = 0
    }

    fun adjustRestTimer(seconds: Int) {
        _restTimeLeft.value = (_restTimeLeft.value + seconds).coerceAtLeast(0)

        if (_restTimeLeft.value == 0) {
            timerJob?.cancel()
            _isTimerRunning.value = false
            _showTimeoutDialog.value = true
        }
    }

    fun dismissTimeoutDialog() {
        _showTimeoutDialog.value = false
    }


    fun getRecommendedRoutine(
        context: Context,
        userInputs: FloatArray,
        blueprint: List<RoutineRepository.ExerciseDetail>
    ): List<CalculatedExercise> {
        initModel(context)
        val calculator = RoutineWeightCalculator()
        val aiResult = modelManager!!.predict(userInputs)

        val result = calculator.calculateRoutine(aiResult, blueprint)

        result.forEach { exercise ->
            Log.d("AI_ROUTINE_TEST", "🏋️ 운동 종목: ${exercise.name}")
            exercise.calculatedSets.forEachIndexed { index, set ->
                Log.d(
                    "AI_ROUTINE_TEST",
                    "   └ [${index + 1}세트] 무게: ${set.weight}kg / 횟수: ${set.reps}회"
                )
            }
        }

        return result
    }

    fun applyAiRecommendedRoutine(
        context: Context,
        userInputs: FloatArray,
        routineName: String,
        onComplete: () -> Unit
    ) {
        initModel(context)
        val aiResult = modelManager?.predict(userInputs) ?: floatArrayOf(0f, 0f, 0f, 0f)

        android.util.Log.d("AI_TEST", "👉 1. AI에게 보낸 입력값: ${userInputs.contentToString()}")
        android.util.Log.d("AI_TEST", "🚨 2. AI가 뱉어낸 진짜 결과값: ${aiResult.contentToString()}")

        val calculator = RoutineWeightCalculator()
        val repoTemplate = RoutineRepository()

        val selectedProgramBlueprint = repoTemplate.allRoutines[routineName] ?: return

        viewModelScope.launch {
            selectedProgramBlueprint.forEach { (dayOfWeek, exerciseBlueprintList) ->

                if (exerciseBlueprintList.isEmpty()) return@forEach

                val newRoutineId = UUID.randomUUID().toString()
                val fullRoutineName = "$routineName - $dayOfWeek"

                repository.insertRoutine(
                    RoutineEntity(id = newRoutineId, name = fullRoutineName)
                )

                val calculatedExercise =
                    calculator.calculateRoutine(aiResult, exerciseBlueprintList)

                val setEntitiesToInsert = mutableListOf<WorkoutSetEntity>()

                calculatedExercise.forEach { exercise ->
                    exercise.calculatedSets.forEachIndexed { index, calculatedSet ->
                        setEntitiesToInsert.add(
                            WorkoutSetEntity(
                                routineId = newRoutineId,
                                exerciseName = exercise.name,
                                weight = calculatedSet.weight,
                                reps = calculatedSet.reps,
                                isChecked = false,
                                restTime = calculatedSet.restTime
                            )
                        )
                    }
                }

                if (setEntitiesToInsert.isNotEmpty()) {
                    repository.insertSets(setEntitiesToInsert)
                }
            }

            loadRoutinesFromDb()


            onComplete()
        }
    }

    fun initModel(context: Context) {
        if (modelManager == null) {
            modelManager = WorkoutModelManager(context)
        }
    }

    override fun onCleared() {
        super.onCleared()
        modelManager?.close()
    }


    fun saveUserProfile(
        age: Int,
        weight: Float,
        gender: String
    ) {
        viewModelScope.launch {

            val profile = UserProfileEntity(
                age = age,
                weight = weight,
                gender = gender
            )

            repository.saveUserProfile(profile)

            _userProfile.value = profile
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _userProfile.value = repository.getUserProfile()
        }
    }

    fun completeRoutineAndUpdateWeights(routineId: String) {

        val routine =
            _routines.value.find { it.id == routineId }
                ?: return

        routine.exercises.forEach { (exerciseName, sets) ->

            val allSetsSuccess =
                sets.all { it.isChecked }

            val lastSet =
                sets.lastOrNull()
                    ?: return@forEach

            val nextWeight =
                ProgressionManager.calculateNextWeight(
                    routineName = routine.name,
                    exerciseName = exerciseName,
                    currentWeight = lastSet.weight,
                    allSetsSuccess = allSetsSuccess,
                    currentReps = lastSet.reps,
                    targetReps = lastSet.reps
                )

            Log.d(
                "PROGRESSION_TEST",
                "$exerciseName : 현재 ${lastSet.weight}kg -> 다음 $nextWeight kg"
            )
        }
    }

    private suspend fun checkAndCreateNextWeekRoutine(routineId: String) {
        val routine = _routines.value.find { it.id == routineId } ?: return

        val isAllCompleted =
            routine.exercises.values
                .flatten()
                .all { it.isChecked }

        val historyEntities = routine.exercises.flatMap { (exerciseName, sets) ->
            sets.map { set ->
                WorkoutHistoryEntity(
                    routineId = routine.id,
                    routineName = routine.name,
                    exerciseName = exerciseName,
                    weight = set.weight,
                    reps = set.reps,
                    isCompleted = set.isChecked
                )
            }
        }

        repository.insertWorkoutHistory(historyEntities)

        if (!isAllCompleted) return

        val nextRoutineName = "${routine.name} - 다음주"

        val alreadyExists =
            _routines.value.any { it.name == nextRoutineName }

        if (alreadyExists) return

        val nextRoutineId = UUID.randomUUID().toString()

        val nextSetEntities = mutableListOf<WorkoutSetEntity>()

        routine.exercises.forEach { (exerciseName, sets) ->
            val lastSet = sets.lastOrNull() ?: return@forEach

            val nextWeight =
                ProgressionManager.calculateNextWeight(
                    routineName = routine.name,
                    exerciseName = exerciseName,
                    currentWeight = lastSet.weight,
                    allSetsSuccess = true,
                    currentReps = lastSet.reps,
                    targetReps = lastSet.reps
                )

            sets.forEach { set ->
                nextSetEntities.add(
                    WorkoutSetEntity(
                        routineId = nextRoutineId,
                        exerciseName = exerciseName,
                        weight = nextWeight,
                        reps = set.reps,
                        isChecked = false,
                        restTime = set.restTime
                    )
                )
            }
        }

        repository.insertRoutine(
            RoutineEntity(
                id = nextRoutineId,
                name = nextRoutineName
            )
        )

        if (nextSetEntities.isNotEmpty()) {
            repository.insertSets(nextSetEntities)
        }
        repository.updateRoutine(
            RoutineEntity(
                id = routine.id,
                name = routine.name,
                isCompleted = true,
                nextRoutineGenerated = true
            )
        )

    }

    private fun loadWorkoutHistory() {
        viewModelScope.launch {
            repository.getAllWorkoutHistory().collect {
                _workoutHistory.value = it
            }
        }
    }

    fun backupWorkoutHistoryToFirebase(userId: String = "test_user") {
        viewModelScope.launch {

            Log.d("FIREBASE_TEST", "업로드 개수: ${_workoutHistory.value.size}")

            try {
                repository.backupWorkoutHistoryToFirebase(
                    userId = userId,
                    historyList = _workoutHistory.value
                )

                Log.d("FIREBASE_BACKUP", "운동 히스토리 백업 성공")
            } catch (e: Exception) {
                Log.e("FIREBASE_BACKUP", "운동 히스토리 백업 실패", e)

            }

        }
    }




}