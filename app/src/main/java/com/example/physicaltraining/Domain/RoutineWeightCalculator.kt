package com.example.physicaltraining.Domain

import com.example.physicaltraining.data.template.RoutineRepository
import kotlin.math.roundToInt

enum class BaseLift {
    BENCH, SQUAT, DEADLIFT, OHP, NONE
}

data class CalculatedSet(
    val weight: Float,
    val reps: Int,
    val restTime: Int
)

data class CalculatedExercise(
    val name: String,
    val baseLift: BaseLift,
    val calculatedSets: List<CalculatedSet>
)

class RoutineWeightCalculator {

    private fun roundToNearest2_5(weight: Float): Float {
        return (weight / 2.5f).roundToInt() * 2.5f
    }

    fun calculateRoutine(
        aiResult: FloatArray,
        routineBlueprint: List<RoutineRepository.ExerciseDetail>
    ): List<CalculatedExercise> {

        val bench1RM = aiResult[0]
        val squat1RM = aiResult[1]
        val deadlift1RM = aiResult[2]
        val aiRestAdjustment = aiResult[3]

        val ohp1RM = bench1RM * 0.65f

        return routineBlueprint.map { exercise ->

            val baseWeight = when (exercise.baseLift) {
                BaseLift.BENCH -> bench1RM
                BaseLift.SQUAT -> squat1RM
                BaseLift.DEADLIFT -> deadlift1RM
                BaseLift.OHP -> ohp1RM
                BaseLift.NONE -> 0f
            }

            // 💡 꼼수(getattr) 버리고 1단계에서 추가한 변수를 직접 호출!
            val specificExercise1RM = baseWeight * exercise.derivativeRatio

            val calculatedSets = exercise.sets.map { setDetail ->

                val rawWeight = specificExercise1RM * setDetail.ratio

                val finalWeight = if (exercise.baseLift == BaseLift.NONE) {
                    0f
                } else {
                    roundToNearest2_5(rawWeight)
                }


                val finalRestTime = maxOf(30, (exercise.defaultRestTime + aiRestAdjustment).toInt())

                CalculatedSet(
                    weight = finalWeight,
                    reps = setDetail.reps,
                    restTime = finalRestTime
                )
            }

            CalculatedExercise(
                name = exercise.name,
                baseLift = exercise.baseLift,
                calculatedSets = calculatedSets
            )
        }
    }
}