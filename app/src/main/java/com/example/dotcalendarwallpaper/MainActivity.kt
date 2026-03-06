package com.example.dotcalendarwallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Button to launch the live wallpaper
        Button(
            onClick = {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                intent.putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, DotWallpaperService ::class.java)
                )
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Set Wallpaper")
        }

        // WebView for remaining space (optional)
        CalendarWebView(
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CalendarWebView(modifier: Modifier = Modifier) {
    androidx.compose.ui.viewinterop.AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                @Suppress("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl("file:///android_asset/index.html")
            }
        }
    )
}
