package com.example.physicaltraining.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val nextRoutineGenerated: Boolean = false

)

@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["routineId"])]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val setId: Int = 0,
    val routineId: String,
    val exerciseName: String,
    val weight: Float,
    val reps: Int,
    val isChecked: Boolean,
    val restTime: Int = 60
)

@Entity(tableName = "workout_history")
data class WorkoutHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val historyId: Int = 0,

    val routineId: String,
    val routineName: String,
    val exerciseName: String,

    val weight: Float,
    val reps: Int,
    val isCompleted: Boolean,

    val completedAt: Long = System.currentTimeMillis()
)