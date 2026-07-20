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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.menurestoran.model.MenuRepository
import com.example.menurestoran.ui.utils.LocalReduceMotion
import com.example.menurestoran.ui.utils.pressScaleEffect
import com.example.menurestoran.utils.CurrencyVisualTransformation
import com.example.menurestoran.utils.ImageHelper
import com.example.menurestoran.utils.formatToRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val categories = listOf("Makanan", "Minuman")
    val scope = rememberCoroutineScope()
    val reduceMotion = LocalReduceMotion.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImageHelper.copyUriToInternalStorage(context, it)
            if (path != null) imagePath = path
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Edit Menu", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Image Picker Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(MaterialTheme.shapes.medium)
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
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Ganti Foto Menu",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val fields = listOf("Nama Menu", "Harga", "Deskripsi")
                fields.forEachIndexed { index, label ->
                    var fieldVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        if (reduceMotion) {
                            fieldVisible = true
                        } else {
                            delay(index * 80L)
                            fieldVisible = true
                        }
                    }
                    
                    AnimatedVisibility(
                        visible = fieldVisible,
                        enter = if (reduceMotion) fadeIn() else fadeIn() + slideInHorizontally(initialOffsetX = { -50 })
                    ) {
                        when (label) {
                            "Nama Menu" -> OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text(label, style = MaterialTheme.typography.labelLarge) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                textStyle = MaterialTheme.typography.bodyLarge,
                                singleLine = true
                            )
                            "Harga" -> OutlinedTextField(
                                value = price,
                                onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                                label = { Text(label, style = MaterialTheme.typography.labelLarge) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = CurrencyVisualTransformation(),
                                shape = MaterialTheme.shapes.small,
                                textStyle = MaterialTheme.typography.bodyLarge,
                                singleLine = true
                            )
                            "Deskripsi" -> OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text(label, style = MaterialTheme.typography.labelLarge) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = MaterialTheme.shapes.small,
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Text(
                    "Kategori",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelLarge) },
                            shape = MaterialTheme.shapes.extraSmall,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank() && !isSaving) {
                        scope.launch {
                            isSaving = true
                            delay(800)
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
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .pressScaleEffect(),
                enabled = name.isNotBlank() && price.isNotBlank() && !isSaving,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
