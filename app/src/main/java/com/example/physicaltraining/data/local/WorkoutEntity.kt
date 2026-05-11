package com.example.physicaltraining.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val exerciseName: String,
    val weight: Float,
    val reps: Int,
    val setNumber: Int
)