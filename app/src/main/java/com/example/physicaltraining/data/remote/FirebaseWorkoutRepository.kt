package com.example.physicaltraining.data.remote

import com.example.physicaltraining.data.local.WorkoutHistoryEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseWorkoutRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun uploadWorkoutHistory(
        userId: String,
        historyList: List<WorkoutHistoryEntity>
    ) {
        val batch = firestore.batch()

        historyList.forEach { history ->
            val docRef = firestore
                .collection("users")
                .document(userId)
                .collection("workout_history")
                .document(history.historyId.toString())

            val data = hashMapOf(
                "historyId" to history.historyId,
                "routineId" to history.routineId,
                "routineName" to history.routineName,
                "exerciseName" to history.exerciseName,
                "weight" to history.weight,
                "reps" to history.reps,
                "isCompleted" to history.isCompleted,
                "completedAt" to history.completedAt
            )

            batch.set(docRef, data)
        }

        batch.commit().await()
    }
}