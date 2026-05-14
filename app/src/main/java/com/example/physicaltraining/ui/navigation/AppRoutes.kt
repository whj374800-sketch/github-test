package com.example.physicaltraining.ui.navigation

sealed class AppRoute(val route: String) {
    object Home : AppRoute("home")

    object RoutineList : AppRoute("routine_list")

    object CheckList : AppRoute("checklist/{routineId}") {
        fun createRoute(routineId: String) = "checklist/$routineId"
    }

    object Timer : AppRoute("timer/{exerciseName}/{initialTime}") {
        fun createRoute(exerciseName: String, initialTime: Int) = "timer/$exerciseName/$initialTime"
    }

    object Graph : AppRoute("graph")
    object AiSetup : AppRoute("ai_setup")
}