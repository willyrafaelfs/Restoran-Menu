// File: ui/screens/ProfileScreen.kt
package com.example.menurestoran.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, prefs: SharedPreferences, updated: Boolean = false) {
    val context = LocalContext.current
    val name = prefs.getString("name", "Restoran Vibe") ?: "Restoran Vibe"
    val email = prefs.getString("email", "contact@restovibe.com") ?: "contact@restovibe.com"
    val address = prefs.getString("address", "Jl. Kuliner No. 123, Jakarta") ?: "Jl. Kuliner No. 123, Jakarta"
    val description = prefs.getString("description", "Restoran dengan cita rasa otentik dan suasana nyaman.") ?: "Restoran dengan cita rasa otentik dan suasana nyaman."
    val hours = prefs.getString("hours", "09:00 - 21:00") ?: "09:00 - 21:00"

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(updated) {
        if (updated) {
            snackbarHostState.showSnackbar("Profil berhasil diperbarui!")
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("edit_profile") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            @Suppress("DEPRECATION")
            Text(name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            ProfileItem(icon = Icons.Default.Email, label = "Email", value = email)
            
            // Klik Alamat untuk buka Google Maps (Implicit Intent)
            ProfileItem(
                icon = Icons.Default.LocationOn, 
                label = "Alamat", 
                value = address,
                modifier = Modifier.clickable {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }
            )
            
            ProfileItem(icon = Icons.Default.Schedule, label = "Jam Operasional", value = hours)
            ProfileItem(icon = Icons.Default.Info, label = "Deskripsi", value = description)
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
