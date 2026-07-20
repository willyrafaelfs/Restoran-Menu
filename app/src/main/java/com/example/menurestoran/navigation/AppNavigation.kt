// File: navigation/AppNavigation.kt
package com.example.menurestoran.navigation

import android.content.SharedPreferences
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.menurestoran.ui.screens.*
import com.example.menurestoran.ui.utils.LocalReduceMotion

@Composable
fun RestoApp(prefs: SharedPreferences, isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val reduceMotion = LocalReduceMotion.current

    // Slide + Fade Transitions
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        if (reduceMotion) fadeIn(animationSpec = tween(300))
        else slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(400, easing = LinearOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400))
    }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        if (reduceMotion) fadeOut(animationSpec = tween(300))
        else slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(400, easing = LinearOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(400))
    }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        if (reduceMotion) fadeIn(animationSpec = tween(300))
        else slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(400, easing = LinearOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400))
    }

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        if (reduceMotion) fadeOut(animationSpec = tween(300))
        else slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(400, easing = LinearOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(400))
    }

    Scaffold(
        bottomBar = {
            val showBottomBar = currentDestination?.route in listOf("home", "menu", "profile", "profile?updated={updated}")
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple("Home", "home", Icons.Default.Home),
                        Triple("Menu", "menu", Icons.Default.MenuBook),
                        Triple("Profile", "profile", Icons.Default.Person)
                    )
                    items.forEach { (label, route, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route?.startsWith(route) == true } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            composable("splash") {
                SplashScreen(navController)
            }
            composable("home") {
                HomeScreen(navController, prefs, isDarkMode, onThemeToggle)
            }
            composable("menu") {
                MenuScreen(navController, prefs)
            }
            composable("add_menu") {
                AddMenuScreen(navController, prefs)
            }
            composable(
                route = "edit_menu/{menuId}",
                arguments = listOf(navArgument("menuId") { type = NavType.LongType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getLong("menuId") ?: 0L
                EditMenuScreen(navController, prefs, menuId)
            }
            composable(
                route = "detail/{menuId}",
                arguments = listOf(navArgument("menuId") { type = NavType.LongType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getLong("menuId") ?: 0L
                DetailMenuScreen(navController, menuId, prefs)
            }
            composable(
                route = "profile?updated={updated}",
                arguments = listOf(navArgument("updated") { defaultValue = false; type = NavType.BoolType })
            ) { backStackEntry ->
                val updated = backStackEntry.arguments?.getBoolean("updated") ?: false
                ProfileScreen(navController, prefs, updated)
            }
            composable(
                route = "edit_profile"
            ) {
                EditProfileScreen(navController, prefs)
            }
        }
    }
}
