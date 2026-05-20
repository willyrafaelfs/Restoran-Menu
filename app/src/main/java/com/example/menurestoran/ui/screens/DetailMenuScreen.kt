// File: ui/screens/DetailMenuScreen.kt
package com.example.menurestoran.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.menurestoran.model.MenuRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMenuScreen(navController: NavHostController, menuId: Long, prefs: SharedPreferences) {
    val context = LocalContext.current
    val menuList = remember { MenuRepository.getMenu(prefs) }
    val item = menuList.find { it.id == menuId } ?: return

    var rating by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val favorites = remember { mutableStateOf(prefs.getStringSet("favorite_menus", emptySet()) ?: emptySet()) }
    val isFavorite = favorites.value.contains(menuId.toString())
    
    var isHeartClicked by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (isHeartClicked) 1.5f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { isHeartClicked = false }
    )

    val scrollState = rememberLazyListState()
    val headerHeight = 300.dp
    
    val firstItemOffset = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
    val firstItemIndex = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    
    val headerAlpha = remember {
        derivedStateOf {
            if (firstItemIndex.value > 0) 0f
            else (1f - (firstItemOffset.value.toFloat() / 500f)).coerceIn(0f, 1f)
        }
    }
    
    val headerTranslation = remember {
        derivedStateOf {
            if (firstItemIndex.value > 0) 0f
            else (firstItemOffset.value.toFloat() * 0.5f)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Menu") },
            text = { Text("Apakah Anda yakin ingin menghapus ${item.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    MenuRepository.deleteMenuItem(prefs, menuId)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Ya, Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(headerHeight))
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            item.name, 
                            style = MaterialTheme.typography.headlineLarge, 
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp)
                        )
                        IconButton(
                            onClick = {
                                isHeartClicked = true
                                val current = prefs.getStringSet("favorite_menus", emptySet())?.toMutableSet() ?: mutableSetOf()
                                if (isFavorite) current.remove(menuId.toString()) else current.add(menuId.toString())
                                prefs.edit().putStringSet("favorite_menus", current).apply()
                                favorites.value = current
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                modifier = Modifier.scale(heartScale),
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    Text(
                        item.price, 
                        style = MaterialTheme.typography.headlineSmall, 
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star $i",
                                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { rating = i }
                                    .padding(4.dp)
                            )
                        }
                    }
                    Text(
                        text = if (rating > 0) "Rating Anda: $rating/5" else "Berikan Rating",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Kembali ke Menu", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // AsyncImage Parallax Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .graphicsLayer {
                    translationY = -headerTranslation.value
                    alpha = headerAlpha.value
                }
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        TopAppBar(
            title = { 
                if (headerAlpha.value < 0.5f) {
                    Text(item.name)
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { navController.navigate("edit_menu/${item.id}") }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Menu")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus Menu", tint = Color.Red)
                }
                IconButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Cobain deh ${item.name} di Restoran kami, harganya cuma ${item.price}!")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan Menu"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Bagikan")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (headerAlpha.value < 0.1f) MaterialTheme.colorScheme.surface else Color.Transparent
            )
        )
    }
}
