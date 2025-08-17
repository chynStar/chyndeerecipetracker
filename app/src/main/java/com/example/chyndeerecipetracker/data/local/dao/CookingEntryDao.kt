package com.chyndee.chyndeerecipetracker.data.local

import androidx.room.*
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CookingEntryDao {

    @Query("SELECT * FROM cooking_entries ORDER BY cookedAt DESC")
    fun getAllEntries(): Flow<List<CookingEntry>>

    @Query("SELECT * FROM cooking_entries WHERE recipeId = :recipeId ORDER BY cookedAt DESC")
    fun getCookingEntriesForRecipe(recipeId: String): Flow<List<CookingEntry>>

    @Query("SELECT * FROM cooking_entries WHERE id = :id")
    suspend fun getCookingEntryById(id: String): CookingEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: CookingEntry)

    @Update
    suspend fun updateCookingEntry(entry: CookingEntry)

    @Delete
    suspend fun deleteEntry(entry: CookingEntry)

    @Query("DELETE FROM cooking_entries WHERE id = :entryId")
    suspend fun deleteCookingEntry(entryId: String)

    @Query("DELETE FROM cooking_entries WHERE recipeId = :recipeId")
    suspend fun deleteCookingEntriesForRecipe(recipeId: String)
}