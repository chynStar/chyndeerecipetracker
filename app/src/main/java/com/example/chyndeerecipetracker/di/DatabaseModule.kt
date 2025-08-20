package com.chyndee.chyndeerecipetracker.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
        Log.d("DATABASE_MODULE", "🏗️ Creating recipe database...")
        Log.d("DATABASE_MODULE", "📁 Database path: ${context.applicationContext.getDatabasePath("recipe_database").absolutePath}")

        return Room.databaseBuilder(
            context.applicationContext,
            RecipeDatabase::class.java,
            "recipe_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("DATABASE_MODULE", "✅ Database CREATED successfully!")
                    Log.d("DATABASE_MODULE", "📊 Database version: ${db.version}")

                    // Log table creation
                    try {
                        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table'")
                        val tables = mutableListOf<String>()
                        while (cursor.moveToNext()) {
                            tables.add(cursor.getString(0))
                        }
                        cursor.close()
                        Log.d("DATABASE_MODULE", "📋 Tables created: ${tables.joinToString(", ")}")
                    } catch (e: Exception) {
                        Log.e("DATABASE_MODULE", "❌ Error querying tables: ${e.message}")
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d("DATABASE_MODULE", "🔓 Database OPENED successfully!")
                    Log.d("DATABASE_MODULE", "📊 Database version: ${db.version}")
                    Log.d("DATABASE_MODULE", "📁 Database path: ${db.path}")

                    // Log existing data
                    try {
                        val cursor = db.query("SELECT COUNT(*) FROM recipes")
                        if (cursor.moveToFirst()) {
                            val count = cursor.getInt(0)
                            Log.d("DATABASE_MODULE", "📋 Existing recipes count: $count")
                        }
                        cursor.close()
                    } catch (e: Exception) {
                        Log.e("DATABASE_MODULE", "❌ Error counting recipes: ${e.message}")
                    }
                }
            })
            .build().also {
                Log.d("DATABASE_MODULE", "🎯 RecipeDatabase instance created successfully")
            }
    }

    @Provides
    fun provideRecipeDao(database: RecipeDatabase): RecipeDao {
        Log.d("DATABASE_MODULE", "🔗 Providing RecipeDao")
        return database.recipeDao()
    }

    @Provides
    fun provideCookingEntryDao(database: RecipeDatabase): CookingEntryDao {
        Log.d("DATABASE_MODULE", "🔗 Providing CookingEntryDao")
        return database.cookingEntryDao()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeDao: RecipeDao,
        cookingEntryDao: CookingEntryDao
    ): RecipeRepository {
        Log.d("DATABASE_MODULE", "🏪 Creating RecipeRepository")
        return RecipeRepositoryImpl(recipeDao, cookingEntryDao)
    }
}