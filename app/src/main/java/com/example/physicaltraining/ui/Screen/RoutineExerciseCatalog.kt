package com.example.physicaltraining.ui.screen

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.json.JSONObject

data class RoutineExerciseCatalogItem(
    val name: String,
    val matched: Boolean,
    val imageAsset: String?,
    val note: String,
    val description: String
)

fun loadRoutineExerciseCatalog(context: Context): List<RoutineExerciseCatalogItem> {
    val jsonText = context.assets.open("routine_exercise_image_matches.json")
        .bufferedReader()
        .use { it.readText() }

    val exercises = JSONObject(jsonText).getJSONArray("exercises")

    return List(exercises.length()) { index ->
        val item = exercises.getJSONObject(index)
        val name = item.getString("name")

        RoutineExerciseCatalogItem(
            name = name,
            matched = item.getBoolean("matched"),
            imageAsset = item.optString("imageAsset").takeIf { it.isNotBlank() && it != "null" },
            note = item.optString("note"),
            description = buildExerciseDescription(name)
        )
    }
}

fun loadAssetImageBitmap(context: Context, assetPath: String): ImageBitmap? {
    return runCatching {
        context.assets.open(assetPath).use { input ->
            BitmapFactory.decodeStream(input)?.asImageBitmap()
        }
    }.getOrNull()
}

private fun buildExerciseDescription(name: String): String {
    return when {
        name.contains("스쿼트") ->
            "하체와 코어를 함께 사용하는 복합 운동입니다. 발바닥을 안정적으로 고정하고 무릎과 엉덩이를 함께 접어 내려갑니다."
        name.contains("스모 데드리프트") ->
            "넓은 스탠스로 하체와 둔근을 강하게 쓰는 데드리프트 변형입니다. 무릎을 바깥으로 열고 바를 몸 가까이 붙여 들어올립니다."
        name.contains("데드리프트") ->
            "후면 사슬과 등, 둔근을 강하게 쓰는 힙힌지 운동입니다. 허리를 중립으로 유지하고 바를 몸 가까이 붙여 들어올립니다."
        name.contains("인클라인") ->
            "윗가슴과 전면 어깨를 더 많이 쓰는 프레스 운동입니다. 벤치 각도를 유지하고 팔꿈치가 과하게 벌어지지 않게 밀어올립니다."
        name.contains("벤치프레스") ->
            "가슴, 삼두, 전면 어깨를 사용하는 상체 밀기 운동입니다. 견갑을 고정하고 바를 가슴 쪽으로 통제해 내립니다."
        name.contains("오버헤드 프레스") || name.contains("숄더 프레스") ->
            "어깨와 삼두를 중심으로 머리 위로 밀어올리는 운동입니다. 몸통을 단단히 고정하고 바 또는 덤벨을 수직으로 올립니다."
        name.contains("로우") ->
            "등 중앙과 광배근을 당기는 운동입니다. 상체 각도를 유지하고 팔꿈치를 뒤로 보내며 당깁니다."
        name.contains("풀업") || name.contains("렛풀다운") ->
            "광배근과 등 전체를 사용하는 수직 당기기 운동입니다. 어깨를 내리고 가슴을 바 쪽으로 끌어올리는 느낌으로 수행합니다."
        name.contains("레그 익스텐션") ->
            "대퇴사두근을 고립해서 쓰는 머신 운동입니다. 무릎을 펴는 동작을 통제하고 잠깐 수축을 느낍니다."
        name.contains("레그 컬") ->
            "햄스트링을 고립해서 쓰는 머신 운동입니다. 골반을 안정시키고 뒤꿈치를 엉덩이 쪽으로 당깁니다."
        name.contains("레그 프레스") ->
            "하체 전반을 머신으로 밀어내는 운동입니다. 허리가 뜨지 않는 범위에서 무릎을 굽혔다가 밀어냅니다."
        name.contains("레터럴 레이즈") ->
            "측면 어깨를 키우는 고립 운동입니다. 반동을 줄이고 팔꿈치가 손보다 살짝 높게 움직이도록 들어올립니다."
        name.contains("컬") ->
            "이두 또는 햄스트링을 굽히는 패턴의 고립 운동입니다. 목표 근육에 긴장을 유지하며 천천히 반복합니다."
        name.contains("트라이셉스") ->
            "삼두근을 집중적으로 쓰는 팔 펴기 운동입니다. 팔꿈치 위치를 고정하고 전완만 움직이듯 천천히 펴줍니다."
        name.contains("딥스") ->
            "가슴과 삼두를 사용하는 맨몸 밀기 운동입니다. 어깨가 과하게 말리지 않도록 범위를 조절합니다."
        name.contains("플라이") ->
            "가슴을 벌리고 모으는 고립 운동입니다. 팔꿈치를 살짝 굽힌 상태로 가슴 근육의 늘어남과 수축을 느낍니다."
        name.contains("케틀벨 스윙") ->
            "둔근과 햄스트링의 폭발적인 힙힌지를 사용하는 전신 운동입니다. 팔로 드는 대신 엉덩이 힘으로 케틀벨을 보냅니다."
        name.contains("카프") || name.contains("종아리") ->
            "종아리 근육을 타겟으로 하는 운동입니다. 발목을 최대한 올리고 내리며 끝 범위를 천천히 통제합니다."
        else ->
            "루틴에 포함할 수 있는 보조 운동입니다. 목표 근육에 집중하고 무게보다 정확한 자세를 우선합니다."
    }
}
