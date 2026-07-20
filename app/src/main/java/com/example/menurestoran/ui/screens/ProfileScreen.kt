package com.example.menurestoran.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.menurestoran.ui.utils.LocalReduceMotion
import com.example.menurestoran.ui.utils.pressScaleEffect
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, prefs: SharedPreferences, updated: Boolean = false) {
    val context = LocalContext.current
    val name = prefs.getString("name", "Rona Rasa") ?: "Rona Rasa"
    val email = prefs.getString("email", "contact@ronarasa.id") ?: "contact@ronarasa.id"
    val address = prefs.getString("address", "Jl. Heritage No. 1, Yogyakarta") ?: "Jl. Heritage No. 1, Yogyakarta"
    val description = prefs.getString("description", "Warisan Kuliner Jawa dengan Sentuhan Modern.") ?: "Warisan Kuliner Jawa dengan Sentuhan Modern."
    val hours = prefs.getString("hours", "09:00 - 22:00") ?: "09:00 - 22:00"
    val profileUrl = prefs.getString("profile_url", "") ?: ""

    val snackbarHostState = remember { SnackbarHostState() }
    val reduceMotion = LocalReduceMotion.current

    LaunchedEffect(updated) {
        if (updated) {
            snackbarHostState.showSnackbar("Profil berhasil diperbarui!")
        }
    }

    // Entrance Animations
    var fabVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (reduceMotion) {
            fabVisible = true
        } else {
            delay(400)
            fabVisible = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profil Restoran", style = MaterialTheme.typography.headlineMedium) },
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
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = if (reduceMotion) fadeIn() else slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("edit_profile") },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.pressScaleEffect()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                }
            }
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
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(120.dp)
            ) {
                if (profileUrl.isNotEmpty()) {
                    AsyncImage(
                        model = profileUrl,
                        contentDescription = "Foto Profil Restoran",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.padding(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(40.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val profileItems = listOf(
                    Triple(Icons.Default.Email, "Email", email),
                    Triple(Icons.Default.LocationOn, "Alamat", address),
                    Triple(Icons.Default.Schedule, "Jam Operasional", hours),
                    Triple(Icons.Default.Info, "Tentang Kami", description)
                )

                profileItems.forEachIndexed { index, (icon, label, value) ->
                    var itemVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        if (reduceMotion) {
                            itemVisible = true
                        } else {
                            delay(index * 80L)
                            itemVisible = true
                        }
                    }
                    
                    AnimatedVisibility(
                        visible = itemVisible,
                        enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(initialOffsetY = { 20 })
                    ) {
                        ProfileItem(
                            icon = icon,
                            label = label,
                            value = value,
                            modifier = if (label == "Alamat") Modifier.clickable {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            } else Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
