package com.example.physicaltraining.Domain

import com.example.physicaltraining.data.template.RoutineRepository // 패키지 경로에 맞게 확인
import kotlin.math.roundToInt

// 1. 기준이 되는 3대 운동 + OHP + 맨몸 정의
enum class BaseLift {
    BENCH, SQUAT, DEADLIFT, OHP, NONE
}

// 2. 세트별 결과 데이터를 담을 새로운 클래스 정의
data class CalculatedSet(
    val weight: Float, // 2.5kg 단위로 계산된 무게
    val reps: Int      // 해당 세트의 반복 횟수
)

// 3. 계산이 완료되어 화면(UI)과 DB에 뿌려질 최종 운동 데이터 구조
data class CalculatedExercise(
    val name: String,
    val baseLift: BaseLift,
    val calculatedSets: List<CalculatedSet> // 단일 세트 수 대신 세트별 리스트를 가집니다.
)

class RoutineWeightCalculator {

    // 헬스장 원판에 맞게 2.5kg 단위로 반올림해주는 함수
    private fun roundToNearest2_5(weight: Float): Float {
        return (weight / 2.5f).roundToInt() * 2.5f
    }

    // AI 결과값과 '새로운 세트 기반 설계도'를 받아 최종 무게들을 계산하는 핵심 함수
    fun calculateRoutine(
        aiResult: FloatArray,
        routineBlueprint: List<RoutineRepository.ExerciseDetail> // Repository에 선언된 구조 사용
    ): List<CalculatedExercise> {

        // AI가 예측한 1RM 데이터 파싱
        val bench1RM = aiResult[0]
        val squat1RM = aiResult[1]
        val deadlift1RM = aiResult[2]

        // OHP는 모델이 예측하지 않으므로 벤치의 65%로 자동 설정
        val ohp1RM = bench1RM * 0.65f

        // 설계도를 돌면서 종목별 세트 계산 수행
        return routineBlueprint.map { exercise ->

            // 이 운동의 기준 1RM 무게가 무엇인지 매핑
            val baseWeight = when (exercise.baseLift) {
                BaseLift.BENCH -> bench1RM
                BaseLift.SQUAT -> squat1RM
                BaseLift.DEADLIFT -> deadlift1RM
                BaseLift.OHP -> ohp1RM
                BaseLift.NONE -> 0f
            }

            // 하나의 운동 안에 속한 세트 리스트(sets)를 하나씩 돌면서 계산
            val calculatedSets = exercise.sets.map { setDetail ->
                // 기준 무게 * 세트별 강도 비율
                val rawWeight = baseWeight * setDetail.ratio

                // 맨몸 운동이 아니면 2.5kg 단위 반올림 적용
                val finalWeight = if (exercise.baseLift == BaseLift.NONE) {
                    0f
                } else {
                    roundToNearest2_5(rawWeight)
                }

                // 계산된 개별 세트 객체 생성
                CalculatedSet(
                    weight = finalWeight,
                    reps = setDetail.reps
                )
            }

            // 최종적으로 세트 리스트를 포함한 운동 데이터 객체 반환
            CalculatedExercise(
                name = exercise.name,
                baseLift = exercise.baseLift,
                calculatedSets = calculatedSets
            )
        }
    }
}