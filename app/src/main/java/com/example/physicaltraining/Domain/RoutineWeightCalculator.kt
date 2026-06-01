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

    private data class AccessoryWeightRule(
        val baseLift: BaseLift,
        val oneRepMaxRatio: Float
    )

    private val accessoryWeightRules = mapOf(
        "덤벨 플라이" to AccessoryWeightRule(BaseLift.BENCH, 0.25f),
        "라잉 트라이셉스 익스텐션" to AccessoryWeightRule(BaseLift.BENCH, 0.40f),
        "레그 익스텐션" to AccessoryWeightRule(BaseLift.SQUAT, 0.72f),
        "레그 컬" to AccessoryWeightRule(BaseLift.DEADLIFT, 0.52f),
        "렛풀다운" to AccessoryWeightRule(BaseLift.BENCH, 0.84f),
        "바벨 컬" to AccessoryWeightRule(BaseLift.BENCH, 0.40f),
        "사이드 레터럴 레이즈" to AccessoryWeightRule(BaseLift.OHP, 0.22f),
        "시티드 케이블 로우" to AccessoryWeightRule(BaseLift.BENCH, 0.87f),
        "종아리 운동" to AccessoryWeightRule(BaseLift.SQUAT, 0.90f),
        "카프 레이즈" to AccessoryWeightRule(BaseLift.SQUAT, 0.90f),
        "케틀벨 스윙 (맨몸 대체)" to AccessoryWeightRule(BaseLift.DEADLIFT, 0.20f)
    )

    private fun roundToNearest2_5(weight: Float): Float {
        return (weight / 2.5f).roundToInt() * 2.5f
    }

    private fun baseLiftWeight(
        baseLift: BaseLift,
        bench1RM: Float,
        squat1RM: Float,
        deadlift1RM: Float,
        ohp1RM: Float
    ): Float {
        return when (baseLift) {
            BaseLift.BENCH -> bench1RM
            BaseLift.SQUAT -> squat1RM
            BaseLift.DEADLIFT -> deadlift1RM
            BaseLift.OHP -> ohp1RM
            BaseLift.NONE -> 0f
        }
    }

    private fun calculateAccessoryWeight(
        exerciseName: String,
        reps: Int,
        bench1RM: Float,
        squat1RM: Float,
        deadlift1RM: Float,
        ohp1RM: Float
    ): Float {
        val rule = accessoryWeightRules[exerciseName] ?: return 0f
        val baseWeight = baseLiftWeight(rule.baseLift, bench1RM, squat1RM, deadlift1RM, ohp1RM)
        val estimatedOneRepMax = baseWeight * rule.oneRepMaxRatio
        val targetRepWeight = estimatedOneRepMax / (1f + reps / 30f)

        return roundToNearest2_5(targetRepWeight)
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

            val baseWeight = baseLiftWeight(exercise.baseLift, bench1RM, squat1RM, deadlift1RM, ohp1RM)

            // 💡 꼼수(getattr) 버리고 1단계에서 추가한 변수를 직접 호출!
            val specificExercise1RM = baseWeight * exercise.derivativeRatio

            val calculatedSets = exercise.sets.map { setDetail ->

                val rawWeight = specificExercise1RM * setDetail.ratio

                val finalWeight = if (exercise.baseLift == BaseLift.NONE) {
                    calculateAccessoryWeight(
                        exerciseName = exercise.name,
                        reps = setDetail.reps,
                        bench1RM = bench1RM,
                        squat1RM = squat1RM,
                        deadlift1RM = deadlift1RM,
                        ohp1RM = ohp1RM
                    )
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
