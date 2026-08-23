package com.realtor.geeksales.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter fun intentLevelToStr(v: IntentLevel): String = v.name
    @TypeConverter fun strToIntentLevel(s: String): IntentLevel = runCatching { IntentLevel.valueOf(s) }.getOrDefault(IntentLevel.U)

    @TypeConverter fun followResultToStr(v: FollowResult): String = v.name
    @TypeConverter fun strToFollowResult(s: String): FollowResult = runCatching { FollowResult.valueOf(s) }.getOrDefault(FollowResult.PENDING)
}

@Database(
    entities = [
        Customer::class,
        FollowUp::class,
        Tag::class,
        CustomerTagMap::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun followUpDao(): FollowUpDao
    abstract fun tagDao(): TagDao

    companion object {
        const val NAME = "geek_sales.db"

        val MIGRATIONS = arrayOf<androidx.room.migration.Migration>(
            // 未来 schema 变更时在此添加 Migration
        )
    }
}
