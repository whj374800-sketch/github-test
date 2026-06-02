package com.example.physicaltraining.data

import com.example.physicaltraining.data.local.RoutineEntity
import com.example.physicaltraining.data.local.UserProfileEntity
import com.example.physicaltraining.data.local.WorkoutDao
import com.example.physicaltraining.data.local.WorkoutHistoryEntity
import com.example.physicaltraining.data.local.WorkoutSetEntity
import com.example.physicaltraining.data.remote.FirebaseWorkoutRepository
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val firebaseWorkoutRepository: FirebaseWorkoutRepository
) {

    val allRoutines: Flow<List<RoutineEntity>> = workoutDao.getAllRoutines()

    fun getSetsForRoutine(routineId: String): Flow<List<WorkoutSetEntity>> {
        return workoutDao.getSetsForRoutine(routineId)
    }

    suspend fun insertRoutine(routine: RoutineEntity) {
        workoutDao.insertRoutine(routine)
    }

    suspend fun insertSets(sets: List<WorkoutSetEntity>) {
        workoutDao.insertSets(sets)
    }

    suspend fun updateSet(set: WorkoutSetEntity) {
        workoutDao.updateSet(set)
    }

    suspend fun deleteRoutine(routine: RoutineEntity) {
        workoutDao.deleteRoutine(routine)
    }

    suspend fun deleteSetsByExercise(routineId: String, exerciseName: String) {
        workoutDao.deleteSetsByExercise(routineId, exerciseName)
    }

    suspend fun deleteSetById(setId: Int) {
        workoutDao.deleteSetById(setId)
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        workoutDao.saveUserProfile(profile)
    }

    suspend fun getUserProfile(): UserProfileEntity? {
        return workoutDao.getUserProfile()
    }

    suspend fun updateRoutine(routine: RoutineEntity) {
        workoutDao.updateRoutine(routine)
    }

    fun getAllWorkoutHistory(): Flow<List<WorkoutHistoryEntity>> {
        return workoutDao.getAllWorkoutHistory()
    }

    fun getHistoryByExercise(exerciseName: String): Flow<List<WorkoutHistoryEntity>> {
        return workoutDao.getHistoryByExercise(exerciseName)
    }

    suspend fun insertWorkoutHistory(history: List<WorkoutHistoryEntity>) {
        workoutDao.insertWorkoutHistory(history)
    }

    suspend fun deleteWorkoutHistoryById(historyId: Int) {
        workoutDao.deleteWorkoutHistoryById(historyId)
    }

    suspend fun deleteIncompleteWorkoutHistory() {
        workoutDao.deleteIncompleteWorkoutHistory()
    }

    suspend fun backupWorkoutHistoryToFirebase(
        userId: String,
        historyList: List<WorkoutHistoryEntity>
    ) {
        firebaseWorkoutRepository.uploadWorkoutHistory(
            userId = userId,
            historyList = historyList
        )
    }
    suspend fun restoreWorkoutHistoryFromFirebase(userId: String) {
        android.util.Log.d("FIREBASE_RESTORE", "Repository 복원 요청 시작")

        val historyList =
            firebaseWorkoutRepository.downloadWorkoutHistory(userId)

        android.util.Log.d(
            "FIREBASE_RESTORE",
            "Firebase에서 가져온 개수 = ${historyList.size}"
        )

        if (historyList.isNotEmpty()) {
            workoutDao.insertWorkoutHistory(historyList)

            android.util.Log.d(
                "FIREBASE_RESTORE",
                "Room DB에 히스토리 저장 완료"
            )
        } else {
            android.util.Log.d(
                "FIREBASE_RESTORE",
                "가져온 히스토리가 비어 있음"
            )
        }
    }



}
