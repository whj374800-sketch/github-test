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
        val historyCollection = firestore
            .collection("users")
            .document(userId)
            .collection("workout_history")
        val batch = firestore.batch()

        historyCollection
            .get()
            .await()
            .documents
            .forEach { document ->
                batch.delete(document.reference)
            }

        historyList.forEach { history ->
            val docRef = historyCollection
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

    suspend fun downloadWorkoutHistory(userId: String): List<WorkoutHistoryEntity> {
        android.util.Log.d("FIREBASE_RESTORE", "Firestore 다운로드 시작")

        val snapshot = firestore
            .collection("users")
            .document(userId)
            .collection("workout_history")
            .get()
            .await()

        android.util.Log.d(
            "FIREBASE_RESTORE",
            "Firestore 문서 개수 = ${snapshot.documents.size}"
        )

        return snapshot.documents.mapNotNull { document ->
            val routineId = document.getString("routineId") ?: return@mapNotNull null
            val routineName = document.getString("routineName") ?: return@mapNotNull null
            val exerciseName = document.getString("exerciseName") ?: return@mapNotNull null

            val historyId = document.getLong("historyId")?.toInt() ?: 0
            val weight = document.getDouble("weight")?.toFloat() ?: 0f
            val reps = document.getLong("reps")?.toInt() ?: 0
            val isCompleted = document.getBoolean("isCompleted") ?: false
            val completedAt = document.getLong("completedAt") ?: System.currentTimeMillis()

            WorkoutHistoryEntity(
                historyId = historyId,
                routineId = routineId,
                routineName = routineName,
                exerciseName = exerciseName,
                weight = weight,
                reps = reps,
                isCompleted = isCompleted,
                completedAt = completedAt
            )
        }
    }

}
