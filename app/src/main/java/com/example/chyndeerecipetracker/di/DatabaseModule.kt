package com.chyndee.chyndeerecipetracker.di

import android.content.Context
import androidx.room.Room
import com.chyndee.chyndeerecipetracker.data.local.RecipeDatabase
import com.chyndee.chyndeerecipetracker.data.local.RecipeDao
import com.chyndee.chyndeerecipetracker.data.local.CookingEntryDao
import com.chyndee.chyndeerecipetracker.data.repository.RecipeRepository
import com.chyndee.chyndeerecipetracker.data.repository.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(
        @ApplicationContext context: Context
    ): RecipeDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RecipeDatabase::class.java,
            "recipe_database"
        ).build()
    }

    @Provides
    fun provideRecipeDao(database: RecipeDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    fun provideCookingEntryDao(database: RecipeDatabase): CookingEntryDao {
        return database.cookingEntryDao()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeDao: RecipeDao,
        cookingEntryDao: CookingEntryDao
    ): RecipeRepository {
        return RecipeRepositoryImpl(recipeDao, cookingEntryDao)
    }
}