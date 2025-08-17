package com.chyndee.chyndeerecipetracker.data.mapper

import com.chyndee.chyndeerecipetracker.domain.model.Recipe
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry

// Identity mappings since domain models = entities
fun Recipe.toDomain(): Recipe = this

fun Recipe.toEntity(): Recipe = this

fun CookingEntry.toDomain(): CookingEntry = this

fun CookingEntry.toEntity(): CookingEntry = this