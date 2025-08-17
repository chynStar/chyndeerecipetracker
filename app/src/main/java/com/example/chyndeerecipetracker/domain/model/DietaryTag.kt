package com.chyndee.chyndeerecipetracker.domain.model  // ← FIXED: Changed from com.example to com.chyndee

import androidx.compose.ui.graphics.Color

enum class DietaryTag(val displayName: String, val color: Color) {
    GLUTEN_FREE("Gluten-Free", Color(0xFF4CAF50)),
    VEGAN("Vegan", Color(0xFF8BC34A)),
    VEGETARIAN("Vegetarian", Color(0xFF9C27B0)),
    NUT_FREE("Nut-Free", Color(0xFFFF9800)),
    DAIRY_FREE("Dairy-Free", Color(0xFF03DAC5)),
    KETO("Keto", Color(0xFF673AB7)),
    LOW_CARB("Low-Carb", Color(0xFF2196F3)),
    HIGH_PROTEIN("High-Protein", Color(0xFFF44336)),
    PALEO("Paleo", Color(0xFF795748)),
    SUGAR_FREE("Sugar-Free", Color(0xFFE91E63)),
    MEDITERRANEAN("Mediterranean", Color(0xFF3F51B5)),
    ASIAN("Asian", Color(0xFFE91E63))
}