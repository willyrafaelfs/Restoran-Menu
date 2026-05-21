// File: ui/screens/HomeScreen.kt
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController, 
    prefs: SharedPreferences, 
    isDarkMode: Boolean, 
    onThemeToggle: (Boolean) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    val restoName = remember(navBackStackEntry) { prefs.getString("name", "Will Restaurant") ?: "Will Restaurant" }
    val restoAddress = remember(navBackStackEntry) { prefs.getString("address", "Jl. Kuliner No. 123, Jakarta") ?: "Jl. Kuliner No. 123, Jakarta" }
    val restoDesc = remember(navBackStackEntry) { prefs.getString("description", "Restoran dengan cita rasa otentik dan suasana nyaman.") ?: "Restoran dengan cita rasa otentik dan suasana nyaman." }
    val restoHours = remember(navBackStackEntry) { prefs.getString("hours", "09:00 - 22:00") ?: "09:00 - 22:00" }
    val restoBanner = remember(navBackStackEntry) { prefs.getString("banner_url", "") ?: "" }

    val context = LocalContext.current
    var isFabExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(if (isFabExpanded) 45f else 0f)

    val pagerState = rememberPagerState(pageCount = { 2 })

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Selamat Datang Di", style = MaterialTheme.typography.labelMedium)
                        Text(restoName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
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
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:08123456789")
                            }
                            context.startActivity(intent)
                        },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                }
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(restoAddress)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location")
                    }
                }
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 16.dp
            ) { page ->
                if (page == 0) {
                    AsyncImage(
                        model = if (restoBanner.isNotEmpty()) restoBanner else "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&q=80",
                        contentDescription = "Banner Restoran",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(restoAddress, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(restoHours, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                restoDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3
                            )
                        }
                    }
                }
            }

            // Pager Dots
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(2) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Promo Spesial Hari Ini",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PromoCarousel(navController)
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class PromoItem(val title: String, val discount: String, val icon: ImageVector, val color: Color, val menuId: Long)

@Composable
fun PromoCarousel(navController: NavHostController) {
    val promos = listOf(
        PromoItem("Makan Siang Hemat", "Diskon 20%", Icons.Default.LunchDining, Color(0xFFFF7043), 1L),
        PromoItem("Happy Hour Kopi", "Beli 1 Gratis 1", Icons.Default.Coffee, Color(0xFF5C6BC0), 5L),
        PromoItem("Paket Keluarga", "Hemat Rp 50rb", Icons.Default.Groups, Color(0xFF66BB6A), 3L)
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
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(promo.color, promo.color.copy(alpha = 0.7f))
                        )
                    )
                    .clickable { navController.navigate("detail/${promo.menuId}") }
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(promo.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(promo.discount, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                }
                Icon(
                    imageVector = promo.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp),
                    tint = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}
