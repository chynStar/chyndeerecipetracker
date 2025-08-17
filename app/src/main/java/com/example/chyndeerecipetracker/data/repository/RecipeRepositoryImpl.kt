package com.chyndee.chyndeerecipetracker.data.repository

import com.chyndee.chyndeerecipetracker.data.local.RecipeDao
import com.chyndee.chyndeerecipetracker.data.local.CookingEntryDao
import com.chyndee.chyndeerecipetracker.domain.model.Recipe
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val cookingEntryDao: CookingEntryDao
) : RecipeRepository {

    // ===== RECIPE METHODS =====

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipeId: String) {
        // Delete the recipe by ID (you may need to add this method to RecipeDao)
        // For now, assuming you have a deleteRecipeById method
        recipeDao.deleteRecipeById(recipeId)
        // Also delete all cooking entries for this recipe
        cookingEntryDao.deleteCookingEntriesForRecipe(recipeId)
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        return recipeDao.getRecipeById(id)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    // ===== COOKING ENTRY METHODS =====

    override suspend fun insertCookingEntry(cookingEntry: CookingEntry) {
        cookingEntryDao.insertEntry(cookingEntry)
    }

    override suspend fun updateCookingEntry(cookingEntry: CookingEntry) {
        cookingEntryDao.updateCookingEntry(cookingEntry)
    }

    override suspend fun deleteCookingEntry(entryId: String) {
        cookingEntryDao.deleteCookingEntry(entryId)
    }

    override suspend fun getCookingEntryById(id: String): CookingEntry? {
        return cookingEntryDao.getCookingEntryById(id)
    }

    override fun getAllCookingEntries(): Flow<List<CookingEntry>> {
        return cookingEntryDao.getAllEntries()
    }

    override fun getCookingEntriesForRecipe(recipeId: String): Flow<List<CookingEntry>> {
        return cookingEntryDao.getCookingEntriesForRecipe(recipeId)
    }
}