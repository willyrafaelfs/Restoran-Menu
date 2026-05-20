// File: ui/screens/EditMenuScreen.kt
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.menurestoran.utils.CurrencyVisualTransformation
import com.example.menurestoran.utils.formatToRupiah
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.menurestoran.model.MenuItem
import com.example.menurestoran.model.MenuRepository
import com.example.menurestoran.utils.ImageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenuScreen(navController: NavHostController, prefs: SharedPreferences, menuId: Long) {
    val existingMenu = remember { MenuRepository.getMenu(prefs).find { it.id == menuId } }
    
    if (existingMenu == null) {
        navController.popBackStack()
        return
    }

    var name by remember { mutableStateOf(existingMenu.name) }
    var price by remember { mutableStateOf(existingMenu.price.replace(Regex("[^0-9]"), "")) }
    var description by remember { mutableStateOf(existingMenu.description) }
    var category by remember { mutableStateOf(existingMenu.category) }
    var imagePath by remember { mutableStateOf(existingMenu.imageUrl) }

    val context = LocalContext.current
    val categories = listOf("Makanan", "Minuman")

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImageHelper.copyUriToInternalStorage(context, it)
            if (path != null) {
                imagePath = path
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Menu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Image Picker Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imagePath.isNotEmpty()) {
                    AsyncImage(
                        model = imagePath,
                        contentDescription = "Menu Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Pilih Foto Menu", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Menu") },
                modifier = Modifier.fillMaxWidth(),
                isError = name.isBlank()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                label = { Text("Harga") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CurrencyVisualTransformation(),
                isError = price.isBlank()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )
            
            Text("Kategori", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank()) {
                        val updatedMenu = existingMenu.copy(
                            name = name,
                            price = formatToRupiah(price),
                            description = description,
                            imageUrl = imagePath,
                            category = category
                        )
                        MenuRepository.updateMenuItem(prefs, updatedMenu)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && price.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan")
            }
        }
    }
}
