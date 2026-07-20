package com.example.menurestoran.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RonaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // tags, chips
    small = RoundedCornerShape(8.dp),         // buttons
    medium = RoundedCornerShape(16.dp),       // cards
    large = RoundedCornerShape(24.dp),        // bottom sheets
    extraLarge = RoundedCornerShape(32.dp)    // hero cards
)
