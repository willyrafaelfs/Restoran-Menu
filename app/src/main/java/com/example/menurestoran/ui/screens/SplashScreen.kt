package com.example.menurestoran.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.menurestoran.ui.theme.RonaDarkBackground
import com.example.menurestoran.ui.utils.LocalReduceMotion
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(navController: NavHostController) {
    val reduceMotion = LocalReduceMotion.current
    var startAnimation by remember { mutableStateOf(false) }
    var showName by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else if (reduceMotion) 1f else 0.5f,
        animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = if (reduceMotion) tween(300) else tween(800),
        label = "logoAlpha"
    )

    val nameOffset by animateIntOffsetAsState(
        targetValue = if (showName || reduceMotion) IntOffset(0, 0) else IntOffset(0, 30),
        animationSpec = if (reduceMotion) snap() else tween(800),
        label = "nameOffset"
    )
    val nameAlpha by animateFloatAsState(
        targetValue = if (showName) 1f else 0f,
        animationSpec = if (reduceMotion) tween(300) else tween(800),
        label = "nameAlpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (showTagline) 1f else 0f,
        animationSpec = if (reduceMotion) tween(300) else tween(1000),
        label = "taglineAlpha"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (startAnimation) MaterialTheme.colorScheme.background else RonaDarkBackground,
        animationSpec = if (reduceMotion) tween(300) else tween(800),
        label = "bgColor"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        if (reduceMotion) {
            showName = true
            showTagline = true
            delay(1500)
        } else {
            delay(300)
            showName = true
            delay(200)
            showTagline = true
            delay(2000)
        }
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Rona Rasa",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .offset { nameOffset }
                        .alpha(nameAlpha)
                )
                
                Text(
                    text = "Heritage Meets Fine Dining",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.alpha(taglineAlpha)
                )
            }
        }
    }
}
