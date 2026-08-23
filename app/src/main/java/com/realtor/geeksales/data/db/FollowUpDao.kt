package com.realtor.geeksales.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowUpDao {

    @Insert
    suspend fun insert(followUp: FollowUp): Long

    @Query("SELECT * FROM follow_ups WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun observeByCustomer(customerId: Long): Flow<List<FollowUp>>

    @Query("SELECT * FROM follow_ups ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<FollowUp>>

    @Query("SELECT * FROM follow_ups WHERE createdAt >= :sinceMs AND createdAt <= :untilMs ORDER BY createdAt ASC")
    suspend fun getByRange(sinceMs: Long, untilMs: Long): List<FollowUp>

    @Query("SELECT COUNT(*) FROM follow_ups WHERE createdAt >= :dayStart AND createdAt < :dayEnd")
    suspend fun countToday(dayStart: Long, dayEnd: Long): Int
}
