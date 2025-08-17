package com.chyndee.chyndeerecipetracker.data.local

import androidx.room.TypeConverter
import com.chyndee.chyndeerecipetracker.domain.model.DietaryTag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun fromDietaryTagList(value: List<DietaryTag>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toDietaryTagList(value: String): List<DietaryTag> {
        return Gson().fromJson(value, object : TypeToken<List<DietaryTag>>() {}.type)
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }
}
