package com.example.physicaltraining.data

import com.example.physicaltraining.data.local.RoutineEntity
import com.example.physicaltraining.data.local.UserProfileEntity
import com.example.physicaltraining.data.local.WorkoutDao
import com.example.physicaltraining.data.local.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

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

}