package com.example.physicaltraining.Domain

import com.example.physicaltraining.data.template.RoutineRepository // 패키지 경로에 맞게 확인
import kotlin.math.roundToInt


enum class BaseLift {
    BENCH, SQUAT, DEADLIFT, OHP, NONE
}


data class CalculatedSet(
    val weight: Float,
    val reps: Int
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


        val ohp1RM = bench1RM * 0.65f


        return routineBlueprint.map { exercise ->


            val baseWeight = when (exercise.baseLift) {
                BaseLift.BENCH -> bench1RM
                BaseLift.SQUAT -> squat1RM
                BaseLift.DEADLIFT -> deadlift1RM
                BaseLift.OHP -> ohp1RM
                BaseLift.NONE -> 0f
            }


            val calculatedSets = exercise.sets.map { setDetail ->

                val rawWeight = baseWeight * setDetail.ratio


                val finalWeight = if (exercise.baseLift == BaseLift.NONE) {
                    0f
                } else {
                    roundToNearest2_5(rawWeight)
                }


                CalculatedSet(
                    weight = finalWeight,
                    reps = setDetail.reps
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