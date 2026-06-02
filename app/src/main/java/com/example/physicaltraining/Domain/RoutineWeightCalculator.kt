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

data class AddedExercisePrescription(
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val restTime: Int
)

class RoutineWeightCalculator {

    private enum class AddedExerciseType {
        COMPOUND,
        ISOLATION,
        BODYWEIGHT,
        CORE
    }

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

    private fun roundedPositiveWeight(weight: Float): Float {
        return if (weight <= 0f) 0f else roundToNearest2_5(weight).coerceAtLeast(2.5f)
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

    fun calculateAddedExercisePrescription(
        aiResult: FloatArray,
        exerciseName: String,
        goal: String
    ): AddedExercisePrescription {
        val bench1RM = aiResult.getOrNull(0)?.takeIf { it > 0f } ?: 0f
        val squat1RM = aiResult.getOrNull(1)?.takeIf { it > 0f } ?: 0f
        val deadlift1RM = aiResult.getOrNull(2)?.takeIf { it > 0f } ?: 0f
        val ohp1RM = bench1RM * 0.65f

        val exerciseType = classifyAddedExercise(exerciseName)
        val preset = goalPreset(goal, exerciseType)
        val sets = preset.sets
        val reps = preset.reps
        val restTime = preset.restTime

        val weight = if (exerciseType == AddedExerciseType.BODYWEIGHT || exerciseType == AddedExerciseType.CORE) {
            0f
        } else {
            val exerciseOneRepMax = estimateAddedExerciseOneRepMax(
                exerciseName = exerciseName,
                bench1RM = bench1RM,
                squat1RM = squat1RM,
                deadlift1RM = deadlift1RM,
                ohp1RM = ohp1RM
            )
            val targetRepWeight = exerciseOneRepMax / (1f + reps / 30f)
            roundedPositiveWeight(targetRepWeight)
        }

        return AddedExercisePrescription(
            sets = sets,
            reps = reps,
            weight = weight,
            restTime = restTime
        )
    }

    private fun classifyAddedExercise(exerciseName: String): AddedExerciseType {
        return when {
            exerciseName.contains("플랭크") || exerciseName.contains("크런치") ||
                    exerciseName.contains("행잉 레그 레이즈") ->
                AddedExerciseType.CORE
            exerciseName.contains("풀업") || exerciseName.contains("딥스") ||
                    exerciseName.contains("푸쉬업") ->
                AddedExerciseType.BODYWEIGHT
            exerciseName.contains("컬") || exerciseName.contains("레이즈") ||
                    exerciseName.contains("플라이") || exerciseName.contains("익스텐션") ||
                    exerciseName.contains("푸쉬다운") || exerciseName.contains("킥백") ||
                    exerciseName.contains("페이스풀") || exerciseName.contains("펙덱") ||
                    exerciseName.contains("크로스오버") || exerciseName.contains("카프") ||
                    exerciseName.contains("종아리") ->
                AddedExerciseType.ISOLATION
            else ->
                AddedExerciseType.COMPOUND
        }
    }

    private fun goalPreset(goal: String, exerciseType: AddedExerciseType): AddedExercisePrescription {
        return when (goal) {
            "스트렝스" -> when (exerciseType) {
                AddedExerciseType.COMPOUND -> AddedExercisePrescription(sets = 4, reps = 5, weight = 0f, restTime = 180)
                AddedExerciseType.ISOLATION -> AddedExercisePrescription(sets = 3, reps = 8, weight = 0f, restTime = 90)
                AddedExerciseType.BODYWEIGHT -> AddedExercisePrescription(sets = 4, reps = 8, weight = 0f, restTime = 90)
                AddedExerciseType.CORE -> AddedExercisePrescription(sets = 3, reps = 12, weight = 0f, restTime = 60)
            }
            "다이어트" -> when (exerciseType) {
                AddedExerciseType.COMPOUND -> AddedExercisePrescription(sets = 3, reps = 12, weight = 0f, restTime = 60)
                AddedExerciseType.ISOLATION -> AddedExercisePrescription(sets = 3, reps = 15, weight = 0f, restTime = 45)
                AddedExerciseType.BODYWEIGHT -> AddedExercisePrescription(sets = 3, reps = 15, weight = 0f, restTime = 45)
                AddedExerciseType.CORE -> AddedExercisePrescription(sets = 3, reps = 20, weight = 0f, restTime = 45)
            }
            "유지/재활" -> when (exerciseType) {
                AddedExerciseType.COMPOUND -> AddedExercisePrescription(sets = 2, reps = 12, weight = 0f, restTime = 75)
                AddedExerciseType.ISOLATION -> AddedExercisePrescription(sets = 2, reps = 15, weight = 0f, restTime = 60)
                AddedExerciseType.BODYWEIGHT -> AddedExercisePrescription(sets = 2, reps = 12, weight = 0f, restTime = 60)
                AddedExerciseType.CORE -> AddedExercisePrescription(sets = 2, reps = 15, weight = 0f, restTime = 45)
            }
            else -> when (exerciseType) {
                AddedExerciseType.COMPOUND -> AddedExercisePrescription(sets = 3, reps = 10, weight = 0f, restTime = 90)
                AddedExerciseType.ISOLATION -> AddedExercisePrescription(sets = 3, reps = 12, weight = 0f, restTime = 60)
                AddedExerciseType.BODYWEIGHT -> AddedExercisePrescription(sets = 3, reps = 12, weight = 0f, restTime = 60)
                AddedExerciseType.CORE -> AddedExercisePrescription(sets = 3, reps = 15, weight = 0f, restTime = 45)
            }
        }
    }

    private fun estimateAddedExerciseOneRepMax(
        exerciseName: String,
        bench1RM: Float,
        squat1RM: Float,
        deadlift1RM: Float,
        ohp1RM: Float
    ): Float {
        return when {
            exerciseName.contains("인클라인 덤벨") -> bench1RM * 0.55f
            exerciseName.contains("인클라인 벤치") -> bench1RM * 0.82f
            exerciseName.contains("클로즈 그립") -> bench1RM * 0.85f
            exerciseName.contains("체스트 프레스") -> bench1RM * 0.80f
            exerciseName.contains("덤벨 플라이") -> bench1RM * 0.25f
            exerciseName.contains("케이블 크로스오버") -> bench1RM * 0.22f
            exerciseName.contains("펙덱") -> bench1RM * 0.30f
            exerciseName.contains("벤치프레스") -> bench1RM

            exerciseName.contains("프론트 스쿼트") -> squat1RM * 0.85f
            exerciseName.contains("레그 프레스") -> squat1RM * 1.20f
            exerciseName.contains("레그 익스텐션") -> squat1RM * 0.72f
            exerciseName.contains("런지") -> squat1RM * 0.35f
            exerciseName.contains("불가리안") -> squat1RM * 0.30f
            exerciseName.contains("힙 쓰러스트") -> squat1RM * 0.95f
            exerciseName.contains("글루트 브릿지") -> squat1RM * 0.70f
            exerciseName.contains("카프") || exerciseName.contains("종아리") -> squat1RM * 0.90f
            exerciseName.contains("스쿼트") -> squat1RM

            exerciseName.contains("스모 데드리프트") -> deadlift1RM * 0.95f
            exerciseName.contains("루마니안 데드리프트") -> deadlift1RM * 0.75f
            exerciseName.contains("스티프 레그 데드리프트") -> deadlift1RM * 0.70f
            exerciseName.contains("레그 컬") -> deadlift1RM * 0.52f
            exerciseName.contains("케틀벨 스윙") -> deadlift1RM * 0.20f
            exerciseName.contains("티바 로우") -> deadlift1RM * 0.45f
            exerciseName.contains("원암 덤벨 로우") -> deadlift1RM * 0.25f
            exerciseName.contains("머신 로우") -> deadlift1RM * 0.45f
            exerciseName.contains("시티드 케이블 로우") -> deadlift1RM * 0.45f
            exerciseName.contains("바벨 로우") -> deadlift1RM * 0.50f
            exerciseName.contains("데드리프트") -> deadlift1RM

            exerciseName.contains("덤벨 숄더 프레스") -> ohp1RM * 0.65f
            exerciseName.contains("시티드 숄더 프레스") -> ohp1RM * 0.70f
            exerciseName.contains("아놀드 프레스") -> ohp1RM * 0.65f
            exerciseName.contains("사이드 레터럴 레이즈") -> ohp1RM * 0.22f
            exerciseName.contains("프론트 레이즈") -> ohp1RM * 0.22f
            exerciseName.contains("리어 델트") -> ohp1RM * 0.20f
            exerciseName.contains("페이스풀") -> ohp1RM * 0.35f
            exerciseName.contains("오버헤드 프레스") -> ohp1RM

            exerciseName.contains("렛풀다운") -> bench1RM * 0.70f
            exerciseName.contains("라잉 트라이셉스") -> bench1RM * 0.40f
            exerciseName.contains("케이블 푸쉬다운") -> bench1RM * 0.35f
            exerciseName.contains("덤벨 킥백") -> bench1RM * 0.12f
            exerciseName.contains("바벨 컬") -> bench1RM * 0.40f
            exerciseName.contains("해머 컬") -> bench1RM * 0.32f
            exerciseName.contains("프리처 컬") -> bench1RM * 0.30f
            else -> bench1RM * 0.50f
        }
    }
}
