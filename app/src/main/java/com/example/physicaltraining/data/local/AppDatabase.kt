package com.example.physicaltraining.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [
        RoutineEntity::class,
        WorkoutSetEntity::class,
        UserProfileEntity::class,
        WorkoutHistoryEntity::class

    ],

    version = 8,

    exportSchema = false)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun workoutDao(): WorkoutDao

    companion object
    {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN name TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN experience TEXT NOT NULL DEFAULT '초보자'")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN goal TEXT NOT NULL DEFAULT '근비대'")
            }
        }

        fun getDatabase(context: Context): AppDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_database"
                )
                    .addMigrations(MIGRATION_7_8)
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }

}
