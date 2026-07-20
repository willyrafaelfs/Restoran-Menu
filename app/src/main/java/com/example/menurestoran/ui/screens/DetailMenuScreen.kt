package com.example.menurestoran.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.menurestoran.model.MenuRepository
import com.example.menurestoran.ui.utils.LocalReduceMotion
import com.example.menurestoran.ui.utils.pressScaleEffect
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMenuScreen(
    navController: NavHostController,
    menuId: Long,
    prefs: SharedPreferences
) {
    val context = LocalContext.current
    val reduceMotion = LocalReduceMotion.current
    val menuList = remember { MenuRepository.getMenu(prefs) }
    val item = menuList.find { it.id == menuId } ?: return

    var rating by remember { mutableIntStateOf(prefs.getInt("rating_$menuId", 0)) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val favorites = remember {
        mutableStateOf(
            prefs.getStringSet("favorite_menus", emptySet()) ?: emptySet()
        )
    }

    val isFavorite = favorites.value.contains(menuId.toString())
    var isHeartClicked by remember { mutableStateOf(false) }

    val heartScale by animateFloatAsState(
        targetValue = if (isHeartClicked) 1.5f else 1f,
        animationSpec = if (reduceMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isHeartClicked = false },
        label = "heartScale"
    )

    val scrollState = rememberLazyListState()
    val headerHeight = 360.dp

    val firstItemOffset = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
    val firstItemIndex = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    val headerAlpha = remember {
        derivedStateOf {
            if (firstItemIndex.value > 0) 0f
            else (1f - (firstItemOffset.value.toFloat() / 600f)).coerceIn(0f, 1f)
        }
    }

    val headerTranslation = remember {
        derivedStateOf {
            if (firstItemIndex.value > 0 || reduceMotion) 0f
            else firstItemOffset.value.toFloat() * 0.5f
        }
    }

    val imageScale = remember {
        derivedStateOf {
            if (firstItemIndex.value > 0 || reduceMotion) 1f
            else 1f + (firstItemOffset.value.toFloat() / 2000f).coerceIn(0f, 0.15f)
        }
    }

    // Entrance Animations
    var contentVisible by remember { mutableStateOf(false) }
    var priceVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (reduceMotion) {
            contentVisible = true
            priceVisible = true
        } else {
            delay(200)
            contentVisible = true
            delay(200)
            priceVisible = true
        }
    }

    val contentOffset by animateIntOffsetAsState(
        targetValue = if (contentVisible || reduceMotion) IntOffset(0, 0) else IntOffset(0, 40),
        animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "contentOffset"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = if (reduceMotion) tween(300) else tween(500),
        label = "contentAlpha"
    )

    val priceAlpha by animateFloatAsState(
        targetValue = if (priceVisible) 1f else 0f,
        animationSpec = if (reduceMotion) tween(300) else tween(400),
        label = "priceAlpha"
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Menu", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("Apakah Anda yakin ingin menghapus ${item.name}?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(
                    onClick = {
                        MenuRepository.deleteMenuItem(prefs, menuId)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Ya, Hapus", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.zIndex(1f),
                title = {
                    if (headerAlpha.value < 0.5f) {
                        Text(item.name, style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (headerAlpha.value > 0.5f) Color.Black.copy(alpha = 0.3f) else Color.Transparent,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = if (headerAlpha.value > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                actions = {
                    val actionIconTint = if (headerAlpha.value > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
                    val actionBgColor = if (headerAlpha.value > 0.5f) Color.Black.copy(alpha = 0.3f) else Color.Transparent

                    Surface(shape = MaterialTheme.shapes.small, color = actionBgColor) {
                        IconButton(onClick = { navController.navigate("edit_menu/${item.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Menu", tint = actionIconTint)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(shape = MaterialTheme.shapes.small, color = actionBgColor) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Menu", tint = if (headerAlpha.value > 0.5f) Color.White else MaterialTheme.colorScheme.error)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(shape = MaterialTheme.shapes.small, color = actionBgColor) {
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Nikmati kelezatan ${item.name} di Rona Rasa Restaurant! Cuma ${item.price}.")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Menu"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = actionIconTint)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (headerAlpha.value < 0.5f) MaterialTheme.colorScheme.surface.copy(alpha = (1f - headerAlpha.value * 2).coerceIn(0f, 1f)) else Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(headerHeight - 24.dp))
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { contentOffset }
                            .alpha(contentAlpha)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp)
                            )

                            IconButton(
                                onClick = {
                                    isHeartClicked = true
                                    val current = prefs.getStringSet("favorite_menus", emptySet())?.toMutableSet() ?: mutableSetOf()
                                    if (isFavorite) current.remove(menuId.toString())
                                    else current.add(menuId.toString())
                                    prefs.edit().putStringSet("favorite_menus", current).apply()
                                    favorites.value = current
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    modifier = Modifier.scale(heartScale).size(28.dp),
                                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.price,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(priceAlpha)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        RatingBar(
                            rating = rating,
                            onRatingChange = { newRating ->
                                rating = newRating
                                prefs.edit().putInt("rating_$menuId", newRating).apply()
                            }
                        )

                        Text(
                            text = if (rating > 0) "Rating Anda: $rating/5" else "Berikan Rating",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(32.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Deskripsi",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Justify,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .pressScaleEffect(),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Kembali ke Menu", style = MaterialTheme.typography.labelLarge)
                        }

                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            }

            // HEADER IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .graphicsLayer {
                        translationY = -headerTranslation.value
                        alpha = headerAlpha.value
                        scaleX = imageScale.value
                        scaleY = imageScale.value
                    }
            ) {
                AsyncImage(
                    model = if (item.imageUrl.isNotBlank()) item.imageUrl else "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun RatingBar(rating: Int, onRatingChange: (Int) -> Unit) {
    val reduceMotion = LocalReduceMotion.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..5) {
            var starVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                if (reduceMotion) {
                    starVisible = true
                } else {
                    delay(i * 80L)
                    starVisible = true
                }
            }
            
            val starScale by animateFloatAsState(
                targetValue = if (starVisible) 1f else 0f,
                animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
                label = "starScale"
            )

            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Star $i",
                tint = if (i <= rating) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(40.dp)
                    .scale(starScale)
                    .clickable { onRatingChange(i) }
                    .padding(4.dp)
            )
        }
    }
}
