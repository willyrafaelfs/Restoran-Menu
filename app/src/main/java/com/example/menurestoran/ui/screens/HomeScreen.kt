package com.example.menurestoran.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.menurestoran.model.MenuRepository
import com.example.menurestoran.ui.theme.RonaElevation
import com.example.menurestoran.ui.utils.LocalReduceMotion
import com.example.menurestoran.ui.utils.pressScaleEffect
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController, 
    prefs: SharedPreferences, 
    isDarkMode: Boolean, 
    onThemeToggle: (Boolean) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    val rawRestoName = remember(navBackStackEntry) { prefs.getString("name", "Rona Rasa") ?: "Rona Rasa" }
    val restoName = if (rawRestoName.contains("Restaurant")) rawRestoName.replace(" Restaurant", "") else rawRestoName
    val restoAddress = remember(navBackStackEntry) { prefs.getString("address", "Jl. Heritage No. 1, Yogyakarta") ?: "Jl. Heritage No. 1, Yogyakarta" }
    val restoDesc = remember(navBackStackEntry) { prefs.getString("description", "Warisan Kuliner Jawa dengan Sentuhan Modern.") ?: "Warisan Kuliner Jawa dengan Sentuhan Modern." }
    val restoHours = remember(navBackStackEntry) { prefs.getString("hours", "09:00 - 22:00") ?: "09:00 - 22:00" }
    val restoBanner = remember(navBackStackEntry) { prefs.getString("banner_url", "") ?: "" }

    val context = LocalContext.current
    val reduceMotion = LocalReduceMotion.current
    var isFabExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(
        targetValue = if (isFabExpanded) 45f else 0f,
        animationSpec = if (reduceMotion) snap() else spring(),
        label = "fabRotation"
    )

    val pagerState = rememberPagerState(pageCount = { 4 })

    // Smooth Auto-scroll pager
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                if (!reduceMotion) {
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
                    )
                } else {
                    pagerState.scrollToPage(nextPage)
                }
            }
        }
    }

    // Staggered Entrance Animations
    var welcomeVisible by remember { mutableStateOf(false) }
    var nameVisible by remember { mutableStateOf(false) }
    var pagerVisible by remember { mutableStateOf(false) }
    var promoVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (reduceMotion) {
            welcomeVisible = true
            nameVisible = true
            pagerVisible = true
            promoVisible = true
            buttonVisible = true
        } else {
            delay(100)
            welcomeVisible = true
            delay(100)
            nameVisible = true
            delay(100)
            pagerVisible = true
            delay(100)
            promoVisible = true
            delay(100)
            buttonVisible = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { 
                    Text("Rona Rasa", style = MaterialTheme.typography.headlineMedium)
                },
                actions = {
                    IconButton(onClick = { onThemeToggle(!isDarkMode) }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = if (reduceMotion) fadeIn() else slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = if (reduceMotion) fadeOut() else slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:08123456789")
                            }
                            context.startActivity(intent)
                        },
                        shape = MaterialTheme.shapes.small,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                }
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = if (reduceMotion) fadeIn() else slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = if (reduceMotion) fadeOut() else slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(restoAddress)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        shape = MaterialTheme.shapes.small,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location")
                    }
                }
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.pressScaleEffect()
                ) {
                    Icon(
                        Icons.Default.Add, 
                        contentDescription = "More",
                        modifier = Modifier.rotate(fabRotation)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AnimatedVisibility(
                    visible = welcomeVisible,
                    enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    Text(
                        text = "Selamat Datang di",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                
                AnimatedVisibility(
                    visible = nameVisible,
                    enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    Text(
                        text = restoName,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Pager
            AnimatedVisibility(
                visible = pagerVisible,
                enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(initialOffsetY = { 30 })
            ) {
                Column {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        pageSpacing = 16.dp
                    ) { page ->
                        when (page) {
                            0 -> {
                                AsyncImage(
                                    model = if (restoBanner.isNotEmpty()) restoBanner else "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&q=80",
                                    contentDescription = "Banner Restoran",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.extraLarge),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            1 -> {
                                AsyncImage(
                                    model = "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800&q=80",
                                    contentDescription = "Suasana Restoran",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.extraLarge),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            2 -> {
                                AsyncImage(
                                    model = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80",
                                    contentDescription = "Koki Menyiapkan Makanan",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.extraLarge),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            3 -> {
                                Card(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = MaterialTheme.shapes.extraLarge,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(RonaElevation.raisedCard)
                                ) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(restoAddress, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(restoHours, style = MaterialTheme.typography.bodyLarge)
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            restoDesc,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 3
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pager Dots
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(4) { iteration ->
                            val isSelected = pagerState.currentPage == iteration
                            val width by animateDpAsState(
                                targetValue = if (isSelected) 24.dp else 8.dp,
                                animationSpec = if (reduceMotion) snap() else tween(300),
                                label = "dotWidth"
                            )
                            val color by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                animationSpec = if (reduceMotion) snap() else tween(300),
                                label = "dotColor"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(8.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Popular Menus Section
            AnimatedVisibility(
                visible = promoVisible,
                enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(initialOffsetY = { 45 })
            ) {
                Column {
                    Text(
                        text = "Menu Terpopuler",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val menuList = remember { MenuRepository.getMenu(prefs) }
                    val popularMenus = menuList.take(3)

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(popularMenus) { item ->
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clickable { navController.navigate("detail/${item.id}") }
                                    .pressScaleEffect(),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(RonaElevation.raisedCard)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = if (item.imageUrl.isNotBlank()) item.imageUrl else "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = item.price,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            AnimatedVisibility(
                visible = promoVisible,
                enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                Column {
                    Text(
                        text = "Penawaran Istimewa",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PromoCarousel(navController)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class PromoItem(val title: String, val discount: String, val icon: ImageVector, val color: Color, val menuId: Long)

@Composable
fun PromoCarousel(navController: NavHostController) {
    val promos = listOf(
        PromoItem("Makan Siang Hemat", "Diskon 20%", Icons.Default.LunchDining, MaterialTheme.colorScheme.primary, 1L),
        PromoItem("Sate Spesial", "Porsi Kenyang", Icons.Default.Restaurant, MaterialTheme.colorScheme.secondary, 2L),
        PromoItem("Happy Hour Kopi", "Beli 1 Gratis 1", Icons.Default.Coffee, MaterialTheme.colorScheme.tertiary, 4L)
    )
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(promos) { promo ->
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        Brush.linearGradient(
                            listOf(promo.color, promo.color.copy(alpha = 0.8f))
                        )
                    )
                    .pressScaleEffect()
                    .clickable { navController.navigate("detail/${promo.menuId}") }
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(promo.title, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge)
                    Text(promo.discount, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyLarge)
                }
                Icon(
                    imageVector = promo.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp),
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                )
            }
        }
    }
}
