package com.chyndee.chyndeerecipetracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val cookingTime: Int = 0,
    val servings: Int = 1,
    val rating: Float = 0f,
    val dietaryTags: List<DietaryTag> = emptyList(),
    val imageUrl: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)