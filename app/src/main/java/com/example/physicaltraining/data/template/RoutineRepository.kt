package com.example.physicaltraining.data.template

import com.example.physicaltraining.Domain.BaseLift

class RoutineRepository {

    data class SetDetail(
        val ratio : Float,
        val reps : Int
    )

    data class ExerciseDetail(
        val name : String,
        val baseLift: BaseLift,
        val sets : List<SetDetail>,
        val derivativeRatio: Float = 1.0f,
        val defaultRestTime: Int = 90


    )
    // ==========================================
    // 1. nSuns 5/3/1 (주 5일 스트렝스/볼륨)
    // ==========================================
    val nsunsBenchDay = listOf(
        ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(
            SetDetail(0.65f, 8), SetDetail(0.70f, 6), SetDetail(0.75f, 4),
            SetDetail(0.80f, 4), SetDetail(0.85f, 4), SetDetail(0.80f, 5),
            SetDetail(0.75f, 6), SetDetail(0.70f, 7), SetDetail(0.65f, 8)
        ), defaultRestTime = 240),
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(
            SetDetail(0.40f, 6), SetDetail(0.50f, 5), SetDetail(0.60f, 3),
            SetDetail(0.60f, 5), SetDetail(0.60f, 7), SetDetail(0.60f, 4),
            SetDetail(0.60f, 6), SetDetail(0.60f, 8)
        ), defaultRestTime = 120)
    )

    val nsunsSquatDay = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(
            SetDetail(0.65f, 5), SetDetail(0.70f, 5), SetDetail(0.75f, 3),
            SetDetail(0.80f, 3), SetDetail(0.85f, 5), SetDetail(0.80f, 3),
            SetDetail(0.75f, 5), SetDetail(0.70f, 5), SetDetail(0.65f, 5)
        ), defaultRestTime = 240),
        ExerciseDetail("스모 데드리프트", BaseLift.DEADLIFT, listOf(
            SetDetail(0.60f, 3), SetDetail(0.65f, 3), SetDetail(0.70f, 3),
            SetDetail(0.75f, 3), SetDetail(0.80f, 3), SetDetail(0.75f, 3),
            SetDetail(0.70f, 3), SetDetail(0.60f, 3)
        ), defaultRestTime = 240)
    )

    val nsunsOhpDay = listOf(
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(
            SetDetail(0.65f, 8), SetDetail(0.70f, 6), SetDetail(0.75f, 4),
            SetDetail(0.80f, 4), SetDetail(0.85f, 4), SetDetail(0.80f, 5),
            SetDetail(0.75f, 6), SetDetail(0.70f, 7), SetDetail(0.65f, 8)
        ), defaultRestTime = 240),
        ExerciseDetail("인클라인 벤치프레스", BaseLift.BENCH, listOf(
            SetDetail(0.40f, 6), SetDetail(0.50f, 5), SetDetail(0.60f, 3),
            SetDetail(0.60f, 5), SetDetail(0.60f, 7), SetDetail(0.60f, 4),
            SetDetail(0.60f, 6), SetDetail(0.60f, 8)
        ), defaultRestTime = 120)
    )

    val nsunsDeadliftDay = listOf(
        ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(
            SetDetail(0.65f, 8), SetDetail(0.70f, 6), SetDetail(0.75f, 5),
            SetDetail(0.80f, 3), SetDetail(0.85f, 5), SetDetail(0.80f, 3),
            SetDetail(0.75f, 5), SetDetail(0.70f, 6), SetDetail(0.65f, 8)
        ), defaultRestTime = 300),
        ExerciseDetail("프론트 스쿼트", BaseLift.SQUAT, listOf(
            SetDetail(0.30f, 5), SetDetail(0.40f, 5), SetDetail(0.50f, 5),
            SetDetail(0.55f, 5), SetDetail(0.60f, 5), SetDetail(0.55f, 5),
            SetDetail(0.50f, 5)
        ), defaultRestTime = 180)
    )

    val nsunsBenchHeavyDay = listOf(
        ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(
            SetDetail(0.65f, 8), SetDetail(0.70f, 6), SetDetail(0.75f, 4),
            SetDetail(0.80f, 4), SetDetail(0.85f, 4), SetDetail(0.90f, 2),
            SetDetail(0.85f, 3), SetDetail(0.80f, 5), SetDetail(0.75f, 6)
        ), defaultRestTime = 240),
        ExerciseDetail("클로즈 그립 벤치프레스", BaseLift.BENCH, listOf(
            SetDetail(0.40f, 6), SetDetail(0.50f, 5), SetDetail(0.60f, 3),
            SetDetail(0.60f, 5), SetDetail(0.60f, 7), SetDetail(0.60f, 4),
            SetDetail(0.60f, 6), SetDetail(0.60f, 8)
        ), defaultRestTime = 120)
    )

    // ==========================================
    // 2. PHUL (파워/근비대 하이브리드)
    // ==========================================
    val phulUpperPower = listOf(
        ExerciseDetail("바벨 벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.85f, 5), SetDetail(0.85f, 5), SetDetail(0.85f, 5)), defaultRestTime = 180),
        ExerciseDetail("바벨 로우", BaseLift.BENCH, listOf(SetDetail(0.8f, 5), SetDetail(0.8f, 5), SetDetail(0.8f, 5)), defaultRestTime = 180),
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(SetDetail(0.8f, 5), SetDetail(0.8f, 5), SetDetail(0.8f, 5)), defaultRestTime = 180)
    )
    val phulLowerPower = listOf(
        ExerciseDetail("바벨 스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.85f, 5), SetDetail(0.85f, 5), SetDetail(0.85f, 5)), defaultRestTime = 180),
        ExerciseDetail("바벨 데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.85f, 5), SetDetail(0.85f, 5)), defaultRestTime = 240),
        ExerciseDetail("레그 프레스", BaseLift.SQUAT, listOf(SetDetail(0.6f, 10), SetDetail(0.6f, 10)), defaultRestTime = 150)
    )
    val phulRestDay = emptyList<ExerciseDetail>()
    val phulUpperHyper = listOf(
        ExerciseDetail("인클라인 덤벨 프레스", BaseLift.BENCH, listOf(SetDetail(0.65f, 10), SetDetail(0.65f, 10), SetDetail(0.65f, 10)), defaultRestTime = 90),
        ExerciseDetail("렛풀다운", BaseLift.NONE, listOf(SetDetail(0f, 12), SetDetail(0f, 12)), defaultRestTime = 90),
        ExerciseDetail("덤벨 숄더 프레스", BaseLift.OHP, listOf(SetDetail(0.6f, 10), SetDetail(0.6f, 10)), defaultRestTime = 90)
    )
    val phulLowerHyper = listOf(
        ExerciseDetail("프론트 스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.65f, 12), SetDetail(0.65f, 12), SetDetail(0.65f, 12)), defaultRestTime = 120),
        ExerciseDetail("바벨 루마니안 데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.65f, 10), SetDetail(0.65f, 10)), defaultRestTime = 120),
        ExerciseDetail("종아리 운동", BaseLift.NONE, listOf(SetDetail(0f, 15), SetDetail(0f, 15)), defaultRestTime = 60)
    )

    // ==========================================
    // 3. StrongLifts 5x5 (스트렝스 기초)
    // ==========================================
    val strongLiftsA1 = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, List(5) { SetDetail(0.75f, 5) }, defaultRestTime = 240),
        ExerciseDetail("벤치프레스", BaseLift.BENCH, List(5) { SetDetail(0.75f, 5) }, defaultRestTime = 180),
        ExerciseDetail("바벨 로우", BaseLift.BENCH, List(5) { SetDetail(0.7f, 5) }, defaultRestTime = 180)
    )
    val strongLiftsRest = emptyList<ExerciseDetail>()
    val strongLiftsB1 = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, List(5) { SetDetail(0.75f, 5) }, defaultRestTime = 240),
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, List(5) { SetDetail(0.75f, 5) }, defaultRestTime = 180),
        ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.8f, 5)), defaultRestTime = 300)
    )
    val strongLiftsA2 = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, List(5) { SetDetail(0.77f, 5) }, defaultRestTime = 240),
        ExerciseDetail("벤치프레스", BaseLift.BENCH, List(5) { SetDetail(0.77f, 5) }, defaultRestTime = 180),
        ExerciseDetail("바벨 로우", BaseLift.BENCH, List(5) { SetDetail(0.72f, 5) }, defaultRestTime = 180)
    )

    // ==========================================
    // 4. PPL (Push / Pull / Legs 분할 루틴)
    // ==========================================
    val pplPush1 = listOf(
        ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.8f, 5), SetDetail(0.8f, 5), SetDetail(0.8f, 5)), defaultRestTime = 150),
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(SetDetail(0.7f, 10), SetDetail(0.7f, 10)), defaultRestTime = 120),
        ExerciseDetail("딥스", BaseLift.NONE, listOf(SetDetail(0f, 12), SetDetail(0f, 12)), defaultRestTime = 90)
    )
    val pplPull1 = listOf(
        ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.85f, 5)), defaultRestTime = 240),
        ExerciseDetail("풀업", BaseLift.NONE, listOf(SetDetail(0f, 8), SetDetail(0f, 8), SetDetail(0f, 8)), defaultRestTime = 120),
        ExerciseDetail("바벨 로우", BaseLift.BENCH, listOf(SetDetail(0.75f, 8), SetDetail(0.75f, 8)), defaultRestTime = 150)
    )
    val pplLegs = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.8f, 6), SetDetail(0.8f, 6), SetDetail(0.8f, 6)), defaultRestTime = 180),
        ExerciseDetail("레그 컬", BaseLift.NONE, listOf(SetDetail(0f, 12), SetDetail(0f, 12)), defaultRestTime = 75),
        ExerciseDetail("카프 레이즈", BaseLift.NONE, listOf(SetDetail(0f, 15), SetDetail(0f, 15)), defaultRestTime = 60)
    )
    val pplPush2 = listOf(
        ExerciseDetail("인클라인 벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.7f, 10), SetDetail(0.7f, 10), SetDetail(0.7f, 10)), defaultRestTime = 120),
        ExerciseDetail("덤벨 플라이", BaseLift.NONE, listOf(SetDetail(0f, 12), SetDetail(0f, 12)), defaultRestTime = 60)
    )
    val pplPull2 = listOf(
        ExerciseDetail("시티드 케이블 로우", BaseLift.NONE, listOf(SetDetail(0f, 10), SetDetail(0f, 10), SetDetail(0f, 10)), defaultRestTime = 90),
        ExerciseDetail("바벨 컬", BaseLift.NONE, listOf(SetDetail(0f, 12), SetDetail(0f, 12)), defaultRestTime = 60)
    )

    // ==========================================
    // 5. GVT (German Volume Training 고볼륨 루틴)
    // ==========================================
    val gvtChestBack = listOf(
        ExerciseDetail("벤치프레스", BaseLift.BENCH, List(10) { SetDetail(0.6f, 10) }, defaultRestTime = 90),
        ExerciseDetail("바벨 로우", BaseLift.BENCH, List(10) { SetDetail(0.55f, 10) }, defaultRestTime = 90)
    )
    val gvtLegsAbs = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, List(10) { SetDetail(0.6f, 10) }, defaultRestTime = 90),
        ExerciseDetail("레그 컬", BaseLift.NONE, listOf(SetDetail(0f, 10), SetDetail(0f, 10)), defaultRestTime = 75)
    )
    val gvtRestDay = emptyList<ExerciseDetail>()
    val gvtShoulderArms = listOf(
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, List(10) { SetDetail(0.55f, 10) }, defaultRestTime = 90),
        ExerciseDetail("바벨 컬", BaseLift.NONE, List(3) { SetDetail(0f, 10) }, defaultRestTime = 60)
    )

    // ==========================================
    // 6. 보디빌딩 분할 (5일 부위별 자극 루틴)
    // ==========================================
    val bbChest = listOf(
        ExerciseDetail("바벨 벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.75f, 10), SetDetail(0.75f, 10), SetDetail(0.7f, 12)), defaultRestTime = 120),
        ExerciseDetail("인클라인 덤벨 프레스", BaseLift.BENCH, listOf(SetDetail(0.6f, 12), SetDetail(0.6f, 12)), defaultRestTime = 90)
    )
    val bbBack = listOf(
        ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.8f, 5), SetDetail(0.75f, 8)), defaultRestTime = 240),
        ExerciseDetail("풀업", BaseLift.NONE, listOf(SetDetail(0f, 10), SetDetail(0f, 10)), defaultRestTime = 90)
    )
    val bbShoulders = listOf(
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(SetDetail(0.75f, 8), SetDetail(0.7f, 10), SetDetail(0.65f, 10)), defaultRestTime = 120),
        ExerciseDetail("사이드 레터럴 레이즈", BaseLift.NONE, listOf(SetDetail(0f, 15), SetDetail(0f, 15)), defaultRestTime = 60)
    )
    val bbLegs = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.75f, 10), SetDetail(0.75f, 10), SetDetail(0.7f, 12)), defaultRestTime = 150),
        ExerciseDetail("레그 익스텐션", BaseLift.NONE, listOf(SetDetail(0f, 15), SetDetail(0f, 15)), defaultRestTime = 60)
    )
    val bbArms = listOf(
        ExerciseDetail("바벨 컬", BaseLift.NONE, listOf(SetDetail(0f, 12), SetDetail(0f, 12)), defaultRestTime = 60),
        ExerciseDetail("라잉 트라이셉스 익스텐션", BaseLift.NONE, listOf(SetDetail(0f, 12), SetDetail(0f, 12)), defaultRestTime = 60)
    )

    // ==========================================
    // 7. 파워리프팅 피킹 (스트렝스 극대화)
    // ==========================================
    val plSquatHeavy = listOf(ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.85f, 3), SetDetail(0.9f, 2), SetDetail(0.95f, 1)), defaultRestTime = 300))
    val plBenchHeavy = listOf(ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.85f, 3), SetDetail(0.9f, 2), SetDetail(0.95f, 1)), defaultRestTime = 240))
    val plRestDay = emptyList<ExerciseDetail>()
    val plDeadHeavy = listOf(ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.85f, 3), SetDetail(0.9f, 2), SetDetail(0.95f, 1)), defaultRestTime = 360))
    val plLightSpeed = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.6f, 3), SetDetail(0.6f, 3)), defaultRestTime = 180),
        ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.6f, 3), SetDetail(0.6f, 3)), defaultRestTime = 180)
    )

    // ==========================================
    // 8. 초보자 전신 루틴 (기초 확보)
    // ==========================================
    val begFullDay1 = listOf(ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.65f, 10)), defaultRestTime = 120), ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.65f, 10)), defaultRestTime = 120))
    val begRestDay = emptyList<ExerciseDetail>()
    val begFullDay2 = listOf(ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.65f, 8)), defaultRestTime = 180), ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(SetDetail(0.6f, 10)), defaultRestTime = 120))
    val begFullDay3 = listOf(ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.65f, 10)), defaultRestTime = 120), ExerciseDetail("바벨 로우", BaseLift.BENCH, listOf(SetDetail(0.6f, 10)), defaultRestTime = 120))

    // ==========================================
    // 9. 상하체 분할 파워 (2분할 기반 주5일 확장)
    // ==========================================
    val ulUpperPower = listOf(ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.85f, 3), SetDetail(0.85f, 3)), defaultRestTime = 180), ExerciseDetail("바벨 로우", BaseLift.BENCH, listOf(SetDetail(0.8f, 5)), defaultRestTime = 180))
    val ulLowerPower = listOf(ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.85f, 3), SetDetail(0.85f, 3)), defaultRestTime = 180), ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.85f, 3)), defaultRestTime = 240))
    val ulRestDay = emptyList<ExerciseDetail>()
    val ulUpperHyper = listOf(ExerciseDetail("인클라인 벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.7f, 10)), defaultRestTime = 90), ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(SetDetail(0.65f, 10)), defaultRestTime = 90))
    val ulLowerHyper = listOf(ExerciseDetail("레그 프레스", BaseLift.SQUAT, listOf(SetDetail(0.65f, 12)), defaultRestTime = 90), ExerciseDetail("스티프 레그 데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.6f, 10)), defaultRestTime = 120))

    // ==========================================
    // 10. 유지 관리 (현상 유지 및 리커버리 루틴)
    // ==========================================
    val mainDay1 = listOf(ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.6f, 10), SetDetail(0.6f, 10)), defaultRestTime = 90))
    val mainDay2 = listOf(ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.6f, 10), SetDetail(0.6f, 10)), defaultRestTime = 90))
    val mainRestDay = emptyList<ExerciseDetail>()
    val mainDay3 = listOf(ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.6f, 8), SetDetail(0.6f, 8)), defaultRestTime = 120))
    val mainDay4 = listOf(ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(SetDetail(0.55f, 10), SetDetail(0.55f, 10)), defaultRestTime = 90))

    // ==========================================
    // 11. 무산소 대사 촉진 (스트렝스 + 인터벌 복합)
    // ==========================================
    val hiitDay1 = listOf(ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.8f, 5), SetDetail(0.7f, 8)), defaultRestTime = 60))
    val hiitDay2 = listOf(ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.8f, 5), SetDetail(0.7f, 8)), defaultRestTime = 60))
    val hiitRestDay = emptyList<ExerciseDetail>()
    val hiitDay3 = listOf(ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.8f, 5), SetDetail(0.7f, 6)), defaultRestTime = 75))
    val hiitDay4 = listOf(ExerciseDetail("케틀벨 스윙 (맨몸 대체)", BaseLift.NONE, listOf(SetDetail(0f, 20), SetDetail(0f, 20)), defaultRestTime = 45))

    // ==========================================
    // 12. 5/3/1 BBB (Boring But Big - 스트렝스 + 벌크)
    // ==========================================
    val bbbPress = listOf(
        ExerciseDetail("오버헤드 프레스", BaseLift.OHP, listOf(SetDetail(0.75f, 5), SetDetail(0.85f, 3), SetDetail(0.95f, 1)), defaultRestTime = 240),
        ExerciseDetail("오버헤드 프레스 (BBB)", BaseLift.OHP, List(5) { SetDetail(0.5f, 10) }, defaultRestTime = 90)
    )
    val bbbDead = listOf(
        ExerciseDetail("데드리프트", BaseLift.DEADLIFT, listOf(SetDetail(0.75f, 5), SetDetail(0.85f, 3), SetDetail(0.95f, 1)), defaultRestTime = 300),
        ExerciseDetail("데드리프트 (BBB)", BaseLift.DEADLIFT, List(5) { SetDetail(0.5f, 10) }, defaultRestTime = 90)
    )
    val bbbRestDay = emptyList<ExerciseDetail>()
    val bbbBench = listOf(
        ExerciseDetail("벤치프레스", BaseLift.BENCH, listOf(SetDetail(0.75f, 5), SetDetail(0.85f, 3), SetDetail(0.95f, 1)), defaultRestTime = 240),
        ExerciseDetail("벤치프레스 (BBB)", BaseLift.BENCH, List(5) { SetDetail(0.5f, 10) }, defaultRestTime = 90)
    )
    val bbbSquat = listOf(
        ExerciseDetail("스쿼트", BaseLift.SQUAT, listOf(SetDetail(0.75f, 5), SetDetail(0.85f, 3), SetDetail(0.95f, 1)), defaultRestTime = 240),
        ExerciseDetail("스쿼트 (BBB)", BaseLift.SQUAT, List(5) { SetDetail(0.5f, 10) }, defaultRestTime = 90)
    )

    // ==========================================
    // 맵 구조화 (UI 연동 및 선택 효율을 위한 구성)
    // ==========================================
    val allRoutines = mapOf(
        "nSuns 5/3/1" to mapOf(
            "월요일" to nsunsBenchDay,
            "화요일" to nsunsSquatDay,
            "수요일" to nsunsOhpDay,
            "목요일" to nsunsDeadliftDay,
            "금요일" to nsunsBenchHeavyDay
        ),
        "PHUL 하이브리드" to mapOf(
            "월요일" to phulUpperPower,
            "화요일" to phulLowerPower,
            "수요일" to phulRestDay,
            "목요일" to phulUpperHyper,
            "금요일" to phulLowerHyper
        ),
        "StrongLifts 5x5" to mapOf(
            "월요일" to strongLiftsA1,
            "화요일" to strongLiftsRest,
            "수요일" to strongLiftsB1,
            "목요일" to strongLiftsRest,
            "금요일" to strongLiftsA2
        ),
        "PPL 分割 루틴" to mapOf(
            "월요일" to pplPush1,
            "화요일" to pplPull1,
            "수요일" to pplLegs,
            "목요일" to pplPush2,
            "금요일" to pplPull2
        ),
        "German Volume (GVT)" to mapOf(
            "월요일" to gvtChestBack,
            "화요일" to gvtLegsAbs,
            "수요일" to gvtRestDay,
            "목요일" to gvtShoulderArms,
            "금요일" to gvtRestDay
        ),
        "보디빌딩 5일 분할" to mapOf(
            "월요일" to bbChest,
            "화요일" to bbBack,
            "수요일" to bbShoulders,
            "목요일" to bbLegs,
            "금요일" to bbArms
        ),
        "파워리프팅 피킹" to mapOf(
            "월요일" to plSquatHeavy,
            "화요일" to plBenchHeavy,
            "수요일" to plRestDay,
            "목요일" to plDeadHeavy,
            "금요일" to plLightSpeed
        ),
        "초보자 전신 루틴" to mapOf(
            "월요일" to begFullDay1,
            "화요일" to begRestDay,
            "수요일" to begFullDay2,
            "목요일" to begRestDay,
            "금요일" to begFullDay3
        ),
        "상하체 분할 파워" to mapOf(
            "월요일" to ulUpperPower,
            "화요일" to ulLowerPower,
            "수요일" to ulRestDay,
            "목요일" to ulUpperHyper,
            "금요일" to ulLowerHyper
        ),
        "유지 관리 루틴" to mapOf(
            "월요일" to mainDay1,
            "화요일" to mainDay2,
            "수요일" to mainRestDay,
            "목요일" to mainDay3,
            "금요일" to mainDay4
        ),
        "무산소 대사 촉진" to mapOf(
            "월요일" to hiitDay1,
            "화요일" to hiitDay2,
            "수요일" to hiitRestDay,
            "목요일" to hiitDay3,
            "금요일" to hiitDay4
        ),
        "5/3/1 BBB" to mapOf(
            "월요일" to bbbPress,
            "화요일" to bbbDead,
            "수요일" to bbbRestDay,
            "목요일" to bbbBench,
            "금요일" to bbbSquat
        )
    )



}