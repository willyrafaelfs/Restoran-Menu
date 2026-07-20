// File: MainActivity.kt
package com.example.menurestoran

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.example.menurestoran.model.MenuRepository
import com.example.menurestoran.navigation.RestoApp
import com.example.menurestoran.ui.theme.RonaRasaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("resto_prefs", Context.MODE_PRIVATE)
        
        // Ensure user gets the correct menu list on first run or update
        if (!prefs.contains("menu_initialized_v3")) {
            MenuRepository.resetToDefault(prefs)
            prefs.edit().putBoolean("menu_initialized_v3", true).apply()
        }

        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkMode by remember { 
                mutableStateOf(prefs.getBoolean("dark_mode", systemTheme)) 
            }

            RonaRasaTheme(darkTheme = isDarkMode) {
                RestoApp(
                    prefs = prefs,
                    isDarkMode = isDarkMode,
                    onThemeToggle = { dark ->
                        isDarkMode = dark
                        prefs.edit().putBoolean("dark_mode", dark).apply()
                    }
                )
            }
        }
    }
}
