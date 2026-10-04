package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = ManilaNavyLight,
  onPrimary = Color.White,
  primaryContainer = ManilaNavyDark,
  onPrimaryContainer = Color(0xFFD6E4FF),
  secondary = ManilaGoldAccent,
  onSecondary = Color(0xFF2C2000),
  secondaryContainer = ManilaGoldVariant,
  onSecondaryContainer = Color(0xFF1E1400),
  tertiary = ManilaScarlet,
  onTertiary = Color.White,
  background = ManilaDarkBackground,
  onBackground = Color(0xFFF1F5F9),
  surface = ManilaDarkSurface,
  onSurface = Color(0xFFF1F5F9),
  surfaceVariant = ManilaDarkSurfaceVariant,
  onSurfaceVariant = Color(0xFFCBD5E1),
  outline = ManilaDarkOutline,
)

private val LightColorScheme = lightColorScheme(
  primary = ManilaNavyPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFDCE6FF),
  onPrimaryContainer = ManilaNavyDark,
  secondary = Color(0xFFB8860B),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFF0B3),
  onSecondaryContainer = Color(0xFF4A3B00),
  tertiary = ManilaScarlet,
  onTertiary = Color.White,
  background = ManilaLightBackground,
  onBackground = Color(0xFF0F172A),
  surface = ManilaLightSurface,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = ManilaLightSurfaceVariant,
  onSurfaceVariant = Color(0xFF475569),
  outline = ManilaLightOutline,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted Philippine palette by default
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content,
  )
}
