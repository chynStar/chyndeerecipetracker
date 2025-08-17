package com.chyndee.chyndeerecipetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val cookingTime: Int,
    val servings: Int,
    val rating: Float,
    val dietaryTags: List<String>,
    val imageUrl: String,
    val createdAt: Date,
    val updatedAt: Date
)