package com.realtor.geeksales.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Int? = null
)

@Entity(
    tableName = "customer_tag_map",
    primaryKeys = ["customerId", "tagId"],
    indices = [Index(value = ["tagId"])]
)
data class CustomerTagMap(
    val customerId: Long,
    val tagId: Long
)
