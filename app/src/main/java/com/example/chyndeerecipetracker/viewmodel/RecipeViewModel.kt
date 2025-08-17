package com.chyndee.chyndeerecipetracker.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chyndee.chyndeerecipetracker.domain.model.Recipe
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry
import com.chyndee.chyndeerecipetracker.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State
import com.chyndee.chyndeerecipetracker.SoundNotificationHelper

import java.util.*


class RecipeViewModel : ViewModel() {
    private val _recipes = mutableStateOf<List<Recipe>>(emptyList())
    val recipes: State<List<Recipe>> = _recipes

    private val _cookingHistory = mutableStateOf<List<CookingEntry>>(emptyList())
    val cookingHistory: State<List<CookingEntry>> = _cookingHistory

    fun addRecipe(recipe: Recipe, context: Context? = null) {
        _recipes.value = _recipes.value + recipe

        // Play sound notification
        context?.let {
            SoundNotificationHelper.playRecipeSavedSound(it)
        }

        Log.d("RECIPE", "Recipe added: ${recipe.name}")
    }

    fun updateRecipe(recipe: Recipe, context: Context? = null) {
        _recipes.value = _recipes.value.map {
            if (it.id == recipe.id) recipe else it
        }

        // Play sound for updates too
        context?.let {
            SoundNotificationHelper.playSuccessSound(it)
        }

        Log.d("RECIPE", "Recipe updated: ${recipe.name}")
    }

    // FIXED: Accept Recipe object instead of String for consistency with MainActivity calls
    fun deleteRecipe(recipe: Recipe) {
        _recipes.value = _recipes.value.filter { it.id != recipe.id }
        _cookingHistory.value = _cookingHistory.value.filter { it.recipeId != recipe.id }
    }

    // FIXED: Accept Recipe object and create CookingEntry internally
    fun addCookingEntry(recipe: Recipe) {
        val entry = CookingEntry(
            id = UUID.randomUUID().toString(),
            recipeId = recipe.id,
            cookedAt = Date(),        // ← Correct property name
            personalRating = 0f,      // ← Correct property name
            notes = ""
        )
        _cookingHistory.value = _cookingHistory.value + entry
    }
    // FIXED: Accept CookingEntry object instead of String
    fun deleteCookingEntry(entry: CookingEntry) {
        _cookingHistory.value = _cookingHistory.value.filter { it.id != entry.id }
    }
}