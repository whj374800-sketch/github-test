package com.example.physicaltraining.Domain

import kotlin.math.roundToInt

// 1. 기준이 되는 3대 운동 + OHP(오버헤드프레스) + 맨몸(NONE) 정의
enum class BaseLift {
    BENCH, SQUAT, DEADLIFT, OHP, NONE
}

// 2. 운동 종목 하나의 '설계도' (DB에 저장될 형태)
data class ExerciseDetail(
    val name: String,
    val baseLift: BaseLift,
    val ratio: Float,       // 기준 무게의 몇 %로 할 것인가? (예: 0.8f = 80%)
    val targetSets: Int,    // 목표 세트 수
    val targetReps: Int     // 목표 반복 횟수
)

// 3. 계산이 완료되어 화면(UI)에 뿌려질 최종 운동 데이터
data class CalculatedExercise(
    val name: String,
    val targetWeight: Float, // 계산된 최종 무게 (2.5kg 단위)
    val targetSets: Int,
    val targetReps: Int
)

class RoutineWeightCalculator {

    // 헬스장 원판에 맞게 2.5kg 단위로 반올림해주는 함수 (필수!)
    private fun roundToNearest2_5(weight: Float): Float {
        return (weight / 2.5f).roundToInt() * 2.5f
    }

    // AI 결과값을 받아 최종 루틴 리스트를 뱉어내는 핵심 함수
    fun calculateRoutine(
        aiResult: FloatArray,
        routineBlueprint: List<ExerciseDetail>
    ): List<CalculatedExercise> {

        // AI가 예측한 1RM 데이터 파싱
        val bench1RM = aiResult[0]
        val squat1RM = aiResult[1]
        val deadlift1RM = aiResult[2]

        // [비밀 공식] OHP는 모델이 예측하지 않으므로 벤치의 65%로 자동 설정
        val ohp1RM = bench1RM * 0.65f

        // 설계도를 돌면서 하나씩 계산하여 결과 리스트 생성
        return routineBlueprint.map { exercise ->

            // 이 운동의 기준 무게가 무엇인지 찾기
            val baseWeight = when (exercise.baseLift) {
                BaseLift.BENCH -> bench1RM
                BaseLift.SQUAT -> squat1RM
                BaseLift.DEADLIFT -> deadlift1RM
                BaseLift.OHP -> ohp1RM
                BaseLift.NONE -> 0f // 맨몸 운동은 무게 0
            }

            // 기준 무게 * 비율
            val rawWeight = baseWeight * exercise.ratio

            // 맨몸 운동이 아니면 2.5kg 단위로 반올림 처리
            val finalWeight = if (exercise.baseLift == BaseLift.NONE) {
                0f
            } else {
                roundToNearest2_5(rawWeight)
            }

            // 계산된 객체 반환
            CalculatedExercise(
                name = exercise.name,
                targetWeight = finalWeight,
                targetSets = exercise.targetSets,
                targetReps = exercise.targetReps
            )
        }
    }
}