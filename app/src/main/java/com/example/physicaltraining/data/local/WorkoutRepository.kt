package com.example.physicaltraining.data

import com.example.physicaltraining.data.local.WorkoutDao
import com.example.physicaltraining.data.local.WorkoutEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allWorkouts: Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()
    suspend fun insert(workout: WorkoutEntity) = workoutDao.insertWorkout(workout)
}