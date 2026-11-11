package com.example.rumattire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

internal val splashColorScheme = lightColorScheme(
    surface = Color(0xFF3E2B23)
)

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(colorScheme = splashColorScheme) {
                SplashScreen()
            }
        }

        // Letakkan logika perpindahan ke Login atau Beranda yang sudah ada di sini.
    }
}

@Composable
internal fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Foto kain memenuhi area layar.
            Image(
                painter = painterResource(R.drawable.bg_login_2),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop
            )

            // Lapisan cokelat transparan di atas foto.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xA32F211B))
            )

            // Logo dan tulisan tetap di tengah dengan proporsi aslinya.
            Image(
                painter = painterResource(R.drawable.splash_content),
                contentDescription = "Rum Attire",
                modifier = Modifier.matchParentSize(),
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(
    name = "Splash Rum Attire",
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun SplashScreenPreview() {
    MaterialTheme(colorScheme = splashColorScheme) {
        SplashScreen()
    }
}