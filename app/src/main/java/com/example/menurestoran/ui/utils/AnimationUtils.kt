package com.example.menurestoran.ui.utils

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

val LocalReduceMotion = compositionLocalOf { false }

@Composable
fun isReduceMotionEnabled(): Boolean {
    val context = LocalContext.current
    val resolver = context.contentResolver
    
    // Initial state
    var reduceMotion by remember {
        mutableStateOf(
            Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f) == 0f
        )
    }

    // Observe changes to system animation settings
    DisposableEffect(resolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                reduceMotion = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f) == 0f
            }
        }
        
        resolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            observer
        )
        
        onDispose {
            resolver.unregisterContentObserver(observer)
        }
    }

    return reduceMotion
}
