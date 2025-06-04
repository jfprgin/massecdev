package com.example.loginhttp.features.auth.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.loginhttp.features.auth.data.models.LoginCredentials
import com.example.loginhttp.features.auth.data.models.LoginLog

@Database(entities = [LoginCredentials::class, LoginLog::class], version = 1, exportSchema = false)
abstract class LoginDatabase: RoomDatabase() {
    abstract fun loginDao(): LoginDao

    companion object {
        @Volatile
        private var INSTANCE: LoginDatabase? = null

        fun getDatabase(context: Context): LoginDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LoginDatabase::class.java,
                    "login_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}