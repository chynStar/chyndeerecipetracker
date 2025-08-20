package com.chyndee.chyndeerecipetracker.data.repository

import com.chyndee.chyndeerecipetracker.domain.model.Recipe
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    // Recipe methods
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun getRecipeById(id: String): Recipe?
    fun getAllRecipes(): Flow<List<Recipe>>

    // Cooking entry methods
    suspend fun insertCookingEntry(cookingEntry: CookingEntry)
    suspend fun updateCookingEntry(cookingEntry: CookingEntry)
    suspend fun deleteCookingEntry(entryId: String)
    suspend fun getCookingEntryById(id: String): CookingEntry?
    fun getAllCookingEntries(): Flow<List<CookingEntry>>
    fun getCookingEntriesForRecipe(recipeId: String): Flow<List<CookingEntry>>


}