package com.chyndee.chyndeerecipetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "cooking_entries")
data class CookingEntryEntity(
    @PrimaryKey
    val id: String,
    val recipeId: String,
    val cookedAt: Date,
    val notes: String,
    val personalRating: Float,
    val imageUrl: String
)