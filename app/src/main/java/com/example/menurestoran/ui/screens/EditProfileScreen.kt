// File: ui/screens/EditProfileScreen.kt
package com.example.menurestoran.ui.screens

import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.menurestoran.utils.ImageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, prefs: SharedPreferences) {
    var name by remember { mutableStateOf(prefs.getString("name", "Restoran Vibe") ?: "") }
    var email by remember { mutableStateOf(prefs.getString("email", "contact@restovibe.com") ?: "") }
    var address by remember { mutableStateOf(prefs.getString("address", "Jl. Kuliner No. 123, Jakarta") ?: "") }
    var description by remember { mutableStateOf(prefs.getString("description", "Restoran dengan cita rasa otentik dan suasana nyaman.") ?: "") }
    var hours by remember { mutableStateOf(prefs.getString("hours", "09:00 - 22:00") ?: "") }
    var bannerUrl by remember { mutableStateOf(prefs.getString("banner_url", "") ?: "") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImageHelper.copyUriToInternalStorage(context, it)
            if (path != null) {
                bannerUrl = path
            }
        }
    }

    fun saveData() {
        prefs.edit().apply {
            putString("name", name)
            putString("email", email)
            putString("address", address)
            putString("description", description)
            putString("hours", hours)
            putString("banner_url", bannerUrl)
            apply()
        }
        navController.navigate("profile?updated=true") {
            popUpTo("profile") { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { saveData() }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Pilih Banner Restoran", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Restoran") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )
            OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Jam Buka") }, modifier = Modifier.fillMaxWidth())
            
            Button(onClick = { saveData() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan")
            }
        }
    }
}
