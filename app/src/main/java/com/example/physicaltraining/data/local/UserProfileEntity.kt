package com.example.physicaltraining.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey

    val id : Int = 0,

    val age : Int,

    val weight : Float,

    val gender : String
)