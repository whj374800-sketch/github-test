package com.example.physicaltraining.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)

    @Query("SELECT * FROM routines ORDER BY date DESC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM workout_sets WHERE routineId = :routineId")
    fun getSetsForRoutine(routineId: String): Flow<List<WorkoutSetEntity>>

    @Update
    suspend fun updateSet(set: WorkoutSetEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("DELETE FROM workout_sets WHERE routineId = :routineId AND exerciseName = :exerciseName")
    suspend fun deleteSetsByExercise(routineId: String, exerciseName: String)

    @Query("DELETE FROM workout_sets WHERE setId = :setId")
    suspend fun deleteSetById(setId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 0")
    suspend fun getUserProfile(): UserProfileEntity?
}