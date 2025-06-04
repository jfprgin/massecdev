package com.example.loginhttp.features.auth.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.loginhttp.features.auth.data.models.LoginCredentials
import com.example.loginhttp.features.auth.data.models.LoginLog

@Dao
interface LoginDao {

    // Credentials Table Queries
    @Query("SELECT * FROM login_credentials LIMIT 1")
    suspend fun getSavedCredentials(): LoginCredentials?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredentials(loginCredentials: LoginCredentials)

    @Update
    suspend fun updateCredentials(loginCredentials: LoginCredentials)

    @Query("DELETE FROM login_credentials")
    suspend fun deleteCredentials()

    // Log Table Queries
    @Insert
    suspend fun insertLog(loginLog: LoginLog)

    @Query("SELECT * FROM login_logs ORDER BY dateTime DESC")
    suspend fun getLoginLogs(): List<LoginLog>
}