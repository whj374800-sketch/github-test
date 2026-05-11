package com.example.physicaltraining.ui.navigation

sealed class AppRoute(val route : String) {
    object Home : AppRoute("home")
    object CheckList : AppRoute("checklist")
    object Timer : AppRoute("timer/{exerciseName}") {

    fun createRoute(exerciseName: String) = "timer/$exerciseName"
}
    object Graph : AppRoute("graph")
    object AiSetup : AppRoute("ai_setup")
}