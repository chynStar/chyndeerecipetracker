package com.chyndee.chyndeerecipetracker.domain.model

enum class SortOption(val displayName: String) {
    NAME("Name (A-Z)"),
    RATING("Rating (High to Low)"),
    COOKING_TIME("Cooking Time (Low to High)"),
    CREATED_DATE("Recently Added"),
    LAST_COOKED("Recently Cooked")
}