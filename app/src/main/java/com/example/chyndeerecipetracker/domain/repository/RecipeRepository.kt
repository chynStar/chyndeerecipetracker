

package com.chyndee.chyndeerecipetracker.domain.repository



import com.chyndee.chyndeerecipetracker.domain.model.Recipe
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: String): Flow<Recipe?>
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipe(id: String)

    fun getAllCookingEntries(): Flow<List<CookingEntry>>
    fun getCookingHistoryForRecipe(recipeId: String): Flow<List<CookingEntry>>
    suspend fun insertCookingEntry(entry: CookingEntry)
    suspend fun deleteCookingEntry(id: String)
}

