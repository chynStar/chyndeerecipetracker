package com.chyndee.chyndeerecipetracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "cooking_entries")
data class CookingEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val recipeId: String,
    val cookedAt: Date,
    val personalRating: Float = 0f,
    val notes: String = ""
)