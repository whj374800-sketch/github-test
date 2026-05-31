package com.example.physicaltraining.ui.navigation

sealed class AppRoute(val route: String) {
    object Home : AppRoute("home")

    object RoutineList : AppRoute("routine_list")

    object CheckList : AppRoute("checklist/{routineId}") {
        fun createRoute(routineId: String) = "checklist/$routineId"
    }

    object Timer : AppRoute("timer/{routineId}/{exerciseName}/{initialTime}") {
        fun createRoute(
            routineId: String,
            exerciseName: String,
            initialTime: Int
        ): String {
            return "timer/$routineId/$exerciseName/$initialTime"
        }
    }

    object FreeTimer : AppRoute("free_timer")

    object Graph : AppRoute("graph")

    object AiSetup : AppRoute("ai_setup")
}