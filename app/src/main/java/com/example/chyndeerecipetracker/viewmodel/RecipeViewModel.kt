package com.chyndee.chyndeerecipetracker.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chyndee.chyndeerecipetracker.domain.model.Recipe
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry
import com.chyndee.chyndeerecipetracker.data.repository.RecipeRepository
import androidx.compose.runtime.State
import com.chyndee.chyndeerecipetracker.SoundNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = mutableStateOf<List<Recipe>>(emptyList())
    val recipes: State<List<Recipe>> = _recipes

    private val _cookingHistory = mutableStateOf<List<CookingEntry>>(emptyList())
    val cookingHistory: State<List<CookingEntry>> = _cookingHistory

    init {
        Log.d("VIEWMODEL", "🚀 ViewModel initialized - loading recipes...")
        loadRecipes()
        loadCookingHistory()

        // Force database creation by trying to access it
        viewModelScope.launch {
            try {
                val count = repository.getAllRecipes().first().size
                Log.d("VIEWMODEL", "📊 Initial recipe count: $count")
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Error accessing database: ${e.message}", e)
            }
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            try {
                repository.getAllRecipes().collect { recipeList ->
                    _recipes.value = recipeList
                    Log.d("VIEWMODEL", "📋 Loaded ${recipeList.size} recipes from database")
                    recipeList.forEach { recipe ->
                        Log.d("VIEWMODEL", "  - Recipe: ${recipe.name}")
                    }
                }
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Error loading recipes: ${e.message}", e)
            }
        }
    }

    private fun loadCookingHistory() {
        viewModelScope.launch {
            try {
                repository.getAllCookingEntries().collect { entries ->
                    _cookingHistory.value = entries
                    Log.d("VIEWMODEL", "🍳 Loaded ${entries.size} cooking entries")
                }
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Error loading cooking history: ${e.message}", e)
            }
        }
    }

    fun addRecipe(recipe: Recipe, context: Context? = null) {
        Log.d("SAVE_FLOW", "8. ViewModel addRecipe called: ${recipe.name}")
        viewModelScope.launch {
            try {
                Log.d("SAVE_FLOW", "9. About to call repository.insertRecipe")
                repository.insertRecipe(recipe)
                Log.d("SAVE_FLOW", "10. ✅ Repository insertRecipe completed")

                // Update local state immediately
                _recipes.value = _recipes.value + recipe
                Log.d(
                    "SAVE_FLOW",
                    "11. ✅ Local state updated. Total recipes: ${_recipes.value.size}"
                )

                // Verify the recipe was actually saved
                val allRecipes = repository.getAllRecipes().first()
                Log.d("SAVE_FLOW", "12. 🔍 Database verification: ${allRecipes.size} recipes in DB")

                // Play sound notification
                context?.let {
                    SoundNotificationHelper.playRecipeSavedSound(it)
                }

            } catch (e: Exception) {
                Log.e("SAVE_FLOW", "❌ Error saving recipe: ${e.message}", e)
            }
        }
    }

    fun updateRecipe(recipe: Recipe, context: Context? = null) {
        Log.d("SAVE_FLOW", "8. ViewModel updateRecipe called: ${recipe.name}")
        viewModelScope.launch {
            try {
                Log.d("SAVE_FLOW", "9. About to call repository.updateRecipe")
                repository.updateRecipe(recipe)
                Log.d("SAVE_FLOW", "10. ✅ Repository updateRecipe completed")

                // Update local state
                _recipes.value = _recipes.value.map {
                    if (it.id == recipe.id) recipe else it
                }
                Log.d("SAVE_FLOW", "11. ✅ Recipe updated successfully")

                // Play sound for updates too
                context?.let {
                    SoundNotificationHelper.playSuccessSound(it)
                }

            } catch (e: Exception) {
                Log.e("SAVE_FLOW", "❌ Error updating recipe: ${e.message}", e)
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipe)  // ✅ Pass recipe.id instead of recipe
                _recipes.value = _recipes.value.filter { it.id != recipe.id }
                _cookingHistory.value = _cookingHistory.value.filter { it.recipeId != recipe.id }
                Log.d("VIEWMODEL", "✅ Recipe deleted: ${recipe.name}")
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Error deleting recipe: ${e.message}", e)
            }
        }
    }

    fun addCookingEntry(recipe: Recipe) {
        viewModelScope.launch {
            try {
                val entry = CookingEntry(
                    id = UUID.randomUUID().toString(),
                    recipeId = recipe.id,
                    cookedAt = Date(),
                    personalRating = 0f,
                    notes = ""
                )
                repository.insertCookingEntry(entry)
                _cookingHistory.value = _cookingHistory.value + entry
                Log.d("VIEWMODEL", "✅ Cooking entry added for: ${recipe.name}")
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Error adding cooking entry: ${e.message}", e)
            }
        }
    }

    fun deleteCookingEntry(entry: CookingEntry) {
        viewModelScope.launch {
            try {
                repository.deleteCookingEntry(entry.id)  // ✅ Pass entry.id instead of entry
                _cookingHistory.value = _cookingHistory.value.filter { it.id != entry.id }
                Log.d("VIEWMODEL", "✅ Cooking entry deleted")
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Error deleting cooking entry: ${e.message}", e)
            }
        }
    }
}