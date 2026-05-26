package com.example.physicaltraining.Domain

enum class ProgressionType {
    LINEAR,
    TRAINING_MAX,
    DOUBLE_PROGRESSION,
    VOLUME_DENSITY,
    MAINTENANCE
}

object ProgressionManager {

    val routineProgressionMap = mapOf(

        "nSuns 5/3/1" to ProgressionType.TRAINING_MAX,

        "PHUL 하이브리드" to ProgressionType.DOUBLE_PROGRESSION,

        "StrongLifts 5x5" to ProgressionType.LINEAR,

        "PPL 분할 루틴" to ProgressionType.DOUBLE_PROGRESSION,

        "German Volume (GVT)" to ProgressionType.DOUBLE_PROGRESSION,

        "보디빌딩 5일 분할" to ProgressionType.DOUBLE_PROGRESSION,

        "파워리프팅 피킹" to ProgressionType.TRAINING_MAX,

        "초보자 전신 루틴" to ProgressionType.LINEAR,

        "상하체 분할 파워" to ProgressionType.DOUBLE_PROGRESSION,

        "유지 관리 루틴" to ProgressionType.MAINTENANCE,

        "무산소 대사 촉진" to ProgressionType.VOLUME_DENSITY,

        "5/3/1 BBB" to ProgressionType.TRAINING_MAX
    )

    private fun linearProgression(
        exerciseName: String,
        currentWeight: Float,
        success: Boolean
    ): Float {

        if (!success) return currentWeight

        return when {

            exerciseName.contains("스쿼트") ->
                currentWeight + 5f

            exerciseName.contains("데드") ->
                currentWeight + 5f

            else ->
                currentWeight + 2.5f
        }
    }

    private fun trainingMaxProgression(
        exerciseName: String,
        currentWeight: Float,
        success: Boolean,
        reps: Int
    ): Float {

        if (!success) return currentWeight

        return when {

            reps >= 6 ->
                currentWeight + 7.5f

            reps >= 4 ->
                currentWeight + 5f

            reps >= 2 ->
                currentWeight + 2.5f

            else ->
                currentWeight
        }
    }

    private fun doubleProgression(
        currentWeight: Float,
        success: Boolean,
        currentReps: Int,
        targetReps: Int
    ): Float {

        if (!success) return currentWeight

        return if (currentReps >= targetReps) {
            currentWeight + 2.5f
        } else {
            currentWeight
        }
    }

    fun calculateNextWeight(
        routineName: String,
        exerciseName: String,
        currentWeight: Float,
        allSetsSuccess: Boolean,
        currentReps: Int,
        targetReps: Int
    ): Float {

        val progressionType =
            routineProgressionMap[routineName]
                ?: ProgressionType.LINEAR

        return when (progressionType) {

            ProgressionType.LINEAR -> {
                linearProgression(
                    exerciseName,
                    currentWeight,
                    allSetsSuccess
                )
            }

            ProgressionType.TRAINING_MAX -> {
                trainingMaxProgression(
                    exerciseName,
                    currentWeight,
                    allSetsSuccess,
                    currentReps
                )
            }

            ProgressionType.DOUBLE_PROGRESSION -> {
                doubleProgression(
                    currentWeight,
                    allSetsSuccess,
                    currentReps,
                    targetReps
                )
            }

            ProgressionType.VOLUME_DENSITY -> {
                if (allSetsSuccess)
                    currentWeight + 1.25f
                else
                    currentWeight
            }

            ProgressionType.MAINTENANCE -> {
                currentWeight
            }
        }
    }

}

