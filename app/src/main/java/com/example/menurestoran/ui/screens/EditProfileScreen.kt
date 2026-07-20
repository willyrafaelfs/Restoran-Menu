package com.example.menurestoran.ui.screens

import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.menurestoran.ui.utils.LocalReduceMotion
import com.example.menurestoran.ui.utils.pressScaleEffect
import com.example.menurestoran.utils.ImageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, prefs: SharedPreferences) {
    var name by remember { mutableStateOf(prefs.getString("name", "Rona Rasa") ?: "") }
    var email by remember { mutableStateOf(prefs.getString("email", "contact@ronarasa.id") ?: "") }
    var address by remember { mutableStateOf(prefs.getString("address", "Jl. Heritage No. 1, Yogyakarta") ?: "") }
    var description by remember { mutableStateOf(prefs.getString("description", "Warisan Kuliner Jawa dengan Sentuhan Modern.") ?: "") }
    var hours by remember { mutableStateOf(prefs.getString("hours", "09:00 - 22:00") ?: "") }
    var bannerUrl by remember { mutableStateOf(prefs.getString("banner_url", "") ?: "") }
    var profileUrl by remember { mutableStateOf(prefs.getString("profile_url", "") ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val reduceMotion = LocalReduceMotion.current

    val bannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImageHelper.copyUriToInternalStorage(context, it)
            if (path != null) bannerUrl = path
        }
    }

    val profileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImageHelper.copyUriToInternalStorage(context, it)
            if (path != null) profileUrl = path
        }
    }

    fun saveData() {
        if (isSaving) return
        scope.launch {
            isSaving = true
            delay(800)
            prefs.edit().apply {
                putString("name", name)
                putString("email", email)
                putString("address", address)
                putString("description", description)
                putString("hours", hours)
                putString("banner_url", bannerUrl)
                putString("profile_url", profileUrl)
                apply()
            }
            navController.navigate("profile?updated=true") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { saveData() }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Banner Picker
            var bannerVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                if (reduceMotion) bannerVisible = true
                else {
                    delay(100)
                    bannerVisible = true
                }
            }
            
            AnimatedVisibility(
                visible = bannerVisible,
                enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(initialOffsetY = { -20 })
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    // Banner Picker Section
                    Column {
                        Text(
                            "Banner Restoran",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    bannerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (bannerUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = bannerUrl,
                                    contentDescription = "Banner Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.AddAPhoto,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        "Pilih Banner",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    // Profile Photo Picker Section
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Foto Profil Restoran",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    profileLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = profileUrl,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.AddAPhoto,
                                        contentDescription = "Ganti Foto Profil",
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        "Pilih Foto",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val fields = listOf(
                    "Nama Restoran" to name,
                    "Email" to email,
                    "Alamat" to address,
                    "Deskripsi" to description,
                    "Jam Operasional" to hours
                )

                fields.forEachIndexed { index, (label, value) ->
                    var fieldVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        if (reduceMotion) fieldVisible = true
                        else {
                            delay(200 + index * 50L)
                            fieldVisible = true
                        }
                    }

                    AnimatedVisibility(
                        visible = fieldVisible,
                        enter = if (reduceMotion) fadeIn() else fadeIn() + slideInHorizontally(initialOffsetX = { -50 })
                    ) {
                        OutlinedTextField(
                            value = when (label) {
                                "Nama Restoran" -> name
                                "Email" -> email
                                "Alamat" -> address
                                "Deskripsi" -> description
                                else -> hours
                            },
                            onValueChange = { newValue ->
                                when (label) {
                                    "Nama Restoran" -> name = newValue
                                    "Email" -> email = newValue
                                    "Alamat" -> address = newValue
                                    "Deskripsi" -> description = newValue
                                    else -> hours = newValue
                                }
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelLarge) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            singleLine = label != "Alamat" && label != "Deskripsi",
                            minLines = if (label == "Alamat") 2 else if (label == "Deskripsi") 4 else 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { saveData() }, 
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .pressScaleEffect(),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isSaving
            ) {
                Crossfade(targetState = isSaving, label = "saveButton") { saving ->
                    if (saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Simpan Perubahan", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
