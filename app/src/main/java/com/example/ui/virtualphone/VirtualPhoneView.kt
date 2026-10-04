package com.example.ui.virtualphone

import androidx.compose.animation.AnimatedVisibility
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ManilaConstants
import com.example.model.ManilaLocationState
import com.example.model.SimulationMode
import com.example.model.VirtualApp
import com.example.model.VirtualPhoneState
import com.example.model.WallpaperMode
import com.example.service.LocaleTimezoneEngine
import com.example.ui.theme.ManilaDarkSurface
import com.example.ui.theme.ManilaGoldAccent
import com.example.ui.theme.ManilaNavyPrimary
import com.example.ui.theme.ManilaScarlet
import com.example.ui.theme.PhoneBezelColor
import com.example.ui.theme.PhoneCameraLens
import com.example.ui.theme.PhoneFrameOuter
import com.example.viewmodel.ManilaSimViewModel
import java.util.Locale

/**
 * Live preview screen simulating a virtual Android phone running in Manila, Philippines:
 * - Status bar showing Manila Time (PST/UTC+8) and Philippine carrier (Smart/Globe)
 * - Hardcoded mock GPS at Manila (14.5995, 120.9842)
 * - System locale: en-PH (English - Philippines) with PHP currency formatting
 * - Virtual Android OS with Manila Maps, PAGASA Weather, Commute Guide, and Settings
 */
@Composable
fun VirtualPhoneScreen(
  viewModel: ManilaSimViewModel,
  locationState: ManilaLocationState,
  phoneState: VirtualPhoneState,
  liveManilaTime: String,
  liveManilaSeconds: String,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .padding(12.dp),
    contentAlignment = Alignment.Center
  ) {
    val isWide = maxWidth > 760.dp

    if (isWide) {
      Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Virtual phone hardware frame on left
        Box(
          modifier = Modifier
            .weight(1.1f)
            .fillMaxHeight(),
          contentAlignment = Alignment.Center
        ) {
          VirtualPhoneHardware(
            phoneState = phoneState,
            locationState = locationState,
            liveManilaTime = liveManilaTime,
            viewModel = viewModel,
            modifier = Modifier.widthIn(max = 380.dp)
          )
        }

        // Live Manila Environment & Control Panel on right
        Box(
          modifier = Modifier
            .weight(0.9f)
            .fillMaxHeight()
        ) {
          VirtualPhoneSideConsole(
            viewModel = viewModel,
            locationState = locationState,
            phoneState = phoneState,
            liveManilaSeconds = liveManilaSeconds
          )
        }
      }
    } else {
      // Mobile / Compact: Vertical stacked layout
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        item {
          VirtualPhoneHardware(
            phoneState = phoneState,
            locationState = locationState,
            liveManilaTime = liveManilaTime,
            viewModel = viewModel,
            modifier = Modifier
              .fillMaxWidth(0.92f)
              .height(640.dp)
          )
        }

        item {
          VirtualPhoneQuickDeck(
            viewModel = viewModel,
            locationState = locationState,
            phoneState = phoneState
          )
        }
      }
    }
  }
}

/**
 * Realistic Virtual Smartphone Hardware Chassis:
 * Metallic trim, slim bezels, front camera pill, live status bar, screen content, navigation bar.
 */
@Composable
fun VirtualPhoneHardware(
  phoneState: VirtualPhoneState,
  locationState: ManilaLocationState,
  liveManilaTime: String,
  viewModel: ManilaSimViewModel,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .shadow(16.dp, shape = RoundedCornerShape(38.dp))
      .border(4.dp, PhoneFrameOuter, RoundedCornerShape(38.dp))
      .border(1.5.dp, Color(0xFF52525B), RoundedCornerShape(38.dp))
      .testTag("virtual_phone_frame"),
    shape = RoundedCornerShape(38.dp),
    colors = CardDefaults.cardColors(containerColor = PhoneBezelColor)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .clip(RoundedCornerShape(32.dp))
        .background(Color.Black)
    ) {
      // Screen background based on wallpaper
      VirtualPhoneWallpaper(phoneState.wallpaperMode)

      Column(modifier = Modifier.fillMaxSize()) {
        // Status Bar (Manila Time, Globe/Smart 5G, GPS pin, Battery)
        VirtualStatusBar(
          phoneState = phoneState,
          liveManilaTime = liveManilaTime,
          isGpsMocking = locationState.isMockProviderActive,
          onStatusClick = { viewModel.toggleNotificationShade() }
        )

        // Main Virtual Screen Area
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          when (phoneState.currentApp) {
            VirtualApp.HOME -> VirtualHomeScreen(
              phoneState = phoneState,
              locationState = locationState,
              liveManilaTime = liveManilaTime,
              onOpenApp = { viewModel.openVirtualApp(it) }
            )
            VirtualApp.MAPS -> VirtualMapsScreen(
              locationState = locationState,
              viewModel = viewModel
            )
            VirtualApp.WEATHER -> VirtualWeatherScreen(
              locationState = locationState
            )
            VirtualApp.COMMUTE -> VirtualCommuteScreen(
              locationState = locationState,
              viewModel = viewModel
            )
            VirtualApp.BROWSER -> VirtualBrowserScreen(
              viewModel = viewModel,
              phoneState = phoneState
            )
            VirtualApp.SETTINGS -> VirtualSettingsScreen(
              locationState = locationState,
              phoneState = phoneState,
              viewModel = viewModel
            )
          }

          // Pull-down Notification Shade overlay
          this@Column.AnimatedVisibility(
            visible = phoneState.isNotificationShadeOpen,
            modifier = Modifier.fillMaxSize()
          ) {
            VirtualNotificationShade(
              phoneState = phoneState,
              locationState = locationState,
              liveManilaTime = liveManilaTime,
              viewModel = viewModel
            )
          }
        }

        // Virtual 3-Button Navigation Bar
        VirtualNavigationBar(
          onBack = { viewModel.pressVirtualBack() },
          onHome = { viewModel.pressVirtualHome() },
          onRecents = { viewModel.openVirtualApp(VirtualApp.SETTINGS) }
        )
      }

      // Camera Punch Hole / Dynamic Island at top
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 6.dp)
          .width(72.dp)
          .height(18.dp)
          .clip(CircleShape)
          .background(PhoneCameraLens)
          .border(0.5.dp, Color(0xFF27272A), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(7.dp)
              .clip(CircleShape)
              .background(Color(0xFF0F172A))
          )
          // Ambient sensor
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(Color(0xFF1E293B))
          )
        }
      }
    }
  }
}

/**
 * Wallpaper rendering
 */
@Composable
fun VirtualPhoneWallpaper(wallpaperMode: WallpaperMode) {
  when (wallpaperMode) {
    WallpaperMode.BAY_SUNSET -> {
      Image(
        painter = painterResource(id = R.drawable.manila_bay_wallpaper),
        contentDescription = "Manila Bay Wallpaper",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      // Slight dark gradient scrim for readability
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.45f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.70f)
              )
            )
          )
      )
    }
    WallpaperMode.AZURE_GRADIENT -> {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0xFF0038A8),
                Color(0xFF0A192F),
                Color(0xFF020C1B)
              )
            )
          )
      )
    }
    WallpaperMode.DARK_MINIMAL -> {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF090D16))
      )
    }
  }
}

/**
 * Realistic Android Status Bar showing Philippine Carrier, Manila Clock, GPS Icon, Battery
 */
@Composable
fun VirtualStatusBar(
  phoneState: VirtualPhoneState,
  liveManilaTime: String,
  isGpsMocking: Boolean,
  onStatusClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(30.dp)
      .clickable { onStatusClick() }
      .padding(horizontal = 14.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left: Live Manila Time (PST, UTC+8)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = liveManilaTime,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.SansSerif
      )
      Text(
        text = "PHT",
        color = ManilaGoldAccent,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
      )
    }

    // Right: Carrier, GPS indicator, Wi-Fi, Signal, Battery
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (isGpsMocking) {
        Icon(
          imageVector = Icons.Default.GpsFixed,
          contentDescription = "Mock GPS Active",
          tint = ManilaGoldAccent,
          modifier = Modifier.size(12.dp)
        )
      }
      if (phoneState.isProxyActive) {
        Icon(
          imageVector = Icons.Default.VpnKey,
          contentDescription = "PH Proxy Active",
          tint = Color(0xFF4ADE80),
          modifier = Modifier.size(11.dp)
        )
      }
      if (phoneState.isWifiConnected) {
        Icon(
          imageVector = Icons.Default.Wifi,
          contentDescription = "Wi-Fi",
          tint = Color.White,
          modifier = Modifier.size(13.dp)
        )
      }
      Icon(
        imageVector = Icons.Default.SignalCellular4Bar,
        contentDescription = "5G Carrier Signal",
        tint = Color.White,
        modifier = Modifier.size(13.dp)
      )
      Text(
        text = "5G",
        color = Color(0xFF67E8F9),
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${phoneState.batteryLevel}%",
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Normal
        )
        Icon(
          imageVector = Icons.Default.BatteryFull,
          contentDescription = "Battery",
          tint = Color(0xFF4ADE80),
          modifier = Modifier.size(13.dp)
        )
      }
    }
  }
}

/**
 * Virtual Android 3-Button Navigation Bar
 */
@Composable
fun VirtualNavigationBar(
  onBack: () -> Unit,
  onHome: () -> Unit,
  onRecents: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(42.dp)
      .background(Color.Black.copy(alpha = 0.5f))
      .padding(horizontal = 32.dp),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(
      onClick = onBack,
      modifier = Modifier
        .size(36.dp)
        .testTag("nav_back_button")
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Virtual Back",
        tint = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.size(18.dp)
      )
    }
    IconButton(
      onClick = onHome,
      modifier = Modifier
        .size(36.dp)
        .testTag("nav_home_button")
    ) {
      Box(
        modifier = Modifier
          .size(14.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.85f))
      )
    }
    IconButton(
      onClick = onRecents,
      modifier = Modifier
        .size(36.dp)
        .testTag("nav_recents_button")
    ) {
      Icon(
        imageVector = Icons.Default.CropSquare,
        contentDescription = "Virtual Recents",
        tint = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

/**
 * Virtual Android Home Screen
 */
@Composable
fun VirtualHomeScreen(
  phoneState: VirtualPhoneState,
  locationState: ManilaLocationState,
  liveManilaTime: String,
  onOpenApp: (VirtualApp) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Manila Clock & Locale Widget
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(Color.Black.copy(alpha = 0.45f))
        .padding(12.dp)
    ) {
      Text(
        text = liveManilaTime,
        fontSize = 38.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = 1.sp
      )
      Text(
        text = LocaleTimezoneEngine.formatManilaDateFull(),
        fontSize = 11.sp,
        color = Color.White.copy(alpha = 0.9f),
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = ManilaNavyPrimary.copy(alpha = 0.85f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = ManilaGoldAccent,
              modifier = Modifier.size(11.dp)
            )
            Text(
              text = "Manila (14.5995, 120.9842)",
              fontSize = 10.sp,
              color = Color.White,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
        Surface(
          color = Color(0xFF1E293B).copy(alpha = 0.85f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "31°C ⛅",
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Middle: Philippine Peso e-Wallet Widget
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenApp(VirtualApp.SETTINGS) },
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.82f))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "GCash / Maya PH Balance",
            fontSize = 9.sp,
            color = Color(0xFF94A3B8)
          )
          Text(
            text = LocaleTimezoneEngine.formatPhpCurrency(24850.75),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4ADE80)
          )
        }
        Surface(
          color = ManilaScarlet.copy(alpha = 0.2f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "en-PH Currency",
            color = ManilaGoldAccent,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    // App Icons Grid
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        VirtualAppIcon(
          title = "Manila Maps",
          icon = Icons.Default.Map,
          badge = "GPS",
          backgroundColor = Color(0xFF0284C7),
          onClick = { onOpenApp(VirtualApp.MAPS) }
        )
        VirtualAppIcon(
          title = "PAGASA",
          icon = Icons.Default.Cloud,
          badge = "31°C",
          backgroundColor = Color(0xFF0D9488),
          onClick = { onOpenApp(VirtualApp.WEATHER) }
        )
        VirtualAppIcon(
          title = "Transit PH",
          icon = Icons.Default.DirectionsBus,
          badge = "Jeepney",
          backgroundColor = Color(0xFFD97706),
          onClick = { onOpenApp(VirtualApp.COMMUTE) }
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        VirtualAppIcon(
          title = "PH Browser",
          icon = Icons.Default.Public,
          badge = "PH IP",
          backgroundColor = Color(0xFF16A34A),
          onClick = { onOpenApp(VirtualApp.BROWSER) }
        )
        VirtualAppIcon(
          title = "Settings",
          icon = Icons.Default.Settings,
          badge = "en-PH",
          backgroundColor = Color(0xFF475569),
          onClick = { onOpenApp(VirtualApp.SETTINGS) }
        )
      }
    }

    // Dock (Quick Launchers)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(Color.White.copy(alpha = 0.15f))
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { onOpenApp(VirtualApp.MAPS) }) {
        Icon(Icons.Default.NearMe, "Map", tint = Color.White, modifier = Modifier.size(22.dp))
      }
      IconButton(onClick = { onOpenApp(VirtualApp.WEATHER) }) {
        Icon(Icons.Default.WbSunny, "Weather", tint = ManilaGoldAccent, modifier = Modifier.size(22.dp))
      }
      IconButton(onClick = { onOpenApp(VirtualApp.BROWSER) }) {
        Icon(Icons.Default.Public, "PH Web", tint = Color(0xFF4ADE80), modifier = Modifier.size(22.dp))
      }
      IconButton(onClick = { onOpenApp(VirtualApp.SETTINGS) }) {
        Icon(Icons.Default.Language, "Locale", tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
      }
    }
  }
}

@Composable
fun VirtualAppIcon(
  title: String,
  icon: ImageVector,
  badge: String,
  backgroundColor: Color,
  onClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .testTag("virtual_app_${title.lowercase().replace(" ", "_")}")
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(backgroundColor)
          .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = Color.White,
          modifier = Modifier.size(26.dp)
        )
      }
      Surface(
        color = ManilaScarlet,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(top = 1.dp, end = 1.dp)
      ) {
        Text(
          text = badge,
          color = Color.White,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = title,
      color = Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

/**
 * Interactive Vector Canvas Map of Manila centered on (14.5995, 120.9842)
 * Renders Manila Bay, Pasig River, Roxas Blvd, Luneta, Intramuros, and pulsating GPS marker.
 */
@Composable
fun VirtualMapsScreen(
  locationState: ManilaLocationState,
  viewModel: ManilaSimViewModel,
) {
  var userPanX by remember { mutableFloatStateOf(0f) }
  var userPanY by remember { mutableFloatStateOf(0f) }

  val pulseAnim = rememberInfiniteTransition(label = "pulse")
  val pulseRadius by pulseAnim.animateFloat(
    initialValue = 8f,
    targetValue = 28f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulseRadius"
  )
  val pulseAlpha by pulseAnim.animateFloat(
    initialValue = 0.8f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulseAlpha"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
  ) {
    // App Header Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF1E293B))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { viewModel.pressVirtualHome() },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, "Home", tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Text(
          text = "Manila Live GPS Map",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = if (locationState.isMockProviderActive) Color(0xFF15803D) else Color(0xFFB91C1C),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = if (locationState.isMockProviderActive) "MOCK ON" else "PAUSED",
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }
    }

    // Vector Map Canvas with Gesture Pan
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            change.consume()
            userPanX += dragAmount.x
            userPanY += dragAmount.y
          }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f + userPanX
        val cy = h / 2f + userPanY

        // Background terrain: Manila Urban Land
        drawRect(Color(0xFF1E293B))

        // Manila Bay (West side ocean water)
        val bayPath = Path().apply {
          moveTo(0f, 0f)
          lineTo(cx - 70f, 0f)
          cubicTo(
            cx - 60f, h * 0.25f,
            cx - 90f, h * 0.6f,
            cx - 50f, h
          )
          lineTo(0f, h)
          close()
        }
        drawPath(bayPath, Color(0xFF0369A1))

        // Manila Bay water shimmer lines
        drawLine(
          color = Color(0xFF0284C7).copy(alpha = 0.5f),
          start = Offset(0f, h * 0.3f),
          end = Offset(cx - 75f, h * 0.3f),
          strokeWidth = 2f
        )
        drawLine(
          color = Color(0xFF0284C7).copy(alpha = 0.5f),
          start = Offset(0f, h * 0.7f),
          end = Offset(cx - 65f, h * 0.7f),
          strokeWidth = 2f
        )

        // Pasig River winding across Manila
        val riverPath = Path().apply {
          moveTo(cx - 65f, cy - 30f)
          cubicTo(
            cx - 20f, cy - 40f,
            cx + 30f, cy - 10f,
            w, cy - 25f
          )
        }
        drawPath(
          path = riverPath,
          color = Color(0xFF0284C7),
          style = Stroke(width = 12f)
        )

        // Intramuros Historic Wall (stone fortification polygon)
        val intramurosPath = Path().apply {
          moveTo(cx - 50f, cy - 25f)
          lineTo(cx - 15f, cy - 22f)
          lineTo(cx - 10f, cy + 25f)
          lineTo(cx - 45f, cy + 22f)
          close()
        }
        drawPath(intramurosPath, Color(0xFF78350F).copy(alpha = 0.45f))
        drawPath(intramurosPath, Color(0xFFD97706), style = Stroke(width = 2f))

        // Luneta / Rizal Park Green Sanctuary
        drawRoundRect(
          color = Color(0xFF15803D).copy(alpha = 0.6f),
          topLeft = Offset(cx - 35f, cy + 35f),
          size = Size(65f, 40f),
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f)
        )

        // Major Arterials: Roxas Boulevard (coastal highway)
        val roxasBlvd = Path().apply {
          moveTo(cx - 60f, 0f)
          cubicTo(cx - 50f, h * 0.3f, cx - 80f, h * 0.7f, cx - 40f, h)
        }
        drawPath(roxasBlvd, Color(0xFFFCD116).copy(alpha = 0.75f), style = Stroke(width = 4f))

        // Taft Avenue Arterial
        drawLine(
          color = Color(0xFF94A3B8).copy(alpha = 0.7f),
          start = Offset(cx + 10f, 0f),
          end = Offset(cx + 25f, h),
          strokeWidth = 4f
        )

        // Manila City Hall Anchor Point
        drawCircle(
          color = ManilaScarlet,
          radius = 5f,
          center = Offset(cx + 10f, cy)
        )

        // Active GPS Mock Position Marker
        // Offset proportional to difference from default Manila anchor
        val dLat = (locationState.latitude - ManilaConstants.DEFAULT_LATITUDE) * 6000f
        val dLng = (locationState.longitude - ManilaConstants.DEFAULT_LONGITUDE) * 6000f
        val gpsX = (cx + 10f + dLng.toFloat()).coerceIn(10f, w - 10f)
        val gpsY = (cy - dLat.toFloat()).coerceIn(10f, h - 10f)

        // Pulsating radar wave
        drawCircle(
          color = Color(0xFF38BDF8).copy(alpha = pulseAlpha),
          radius = pulseRadius,
          center = Offset(gpsX, gpsY)
        )
        // Outer glow
        drawCircle(
          color = Color.White,
          radius = 8f,
          center = Offset(gpsX, gpsY)
        )
        // Inner core blue
        drawCircle(
          color = Color(0xFF0284C7),
          radius = 5.5f,
          center = Offset(gpsX, gpsY)
        )

        // Heading directional beam if moving
        if (locationState.speedKmh > 0.5f) {
          val rad = Math.toRadians(locationState.bearing.toDouble())
          val headX = gpsX + (sin(rad) * 22.0).toFloat()
          val headY = gpsY - (cos(rad) * 22.0).toFloat()
          drawLine(
            color = ManilaGoldAccent,
            start = Offset(gpsX, gpsY),
            end = Offset(headX, headY),
            strokeWidth = 3f
          )
        }
      }

      // Telemetry HUD overlay in Maps
      Surface(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(6.dp),
        color = Color(0xFF020617).copy(alpha = 0.88f),
        shape = RoundedCornerShape(10.dp)
      ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.NearMe, null, tint = ManilaGoldAccent, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = locationState.landmarkName,
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Text(
            text = String.format(Locale.US, "%.5f° N, %.5f° E", locationState.latitude, locationState.longitude),
            color = Color(0xFF38BDF8),
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Speed: ${String.format(Locale.US, "%.1f", locationState.speedKmh)} km/h • Alt: ${locationState.altitude}m",
            color = Color(0xFF94A3B8),
            fontSize = 8.5.sp
          )
        }
      }

      // Map Quick Controls (Reset to Manila Anchor)
      Row(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Surface(
          color = ManilaNavyPrimary,
          shape = CircleShape,
          modifier = Modifier
            .clickable {
              userPanX = 0f
              userPanY = 0f
              viewModel.resetToDefaultManila()
            }
        ) {
          Icon(
            Icons.Default.Refresh,
            "Center on Manila",
            tint = Color.White,
            modifier = Modifier
              .padding(8.dp)
              .size(16.dp)
          )
        }
      }
    }

    // Quick landmark teleport bar inside Maps
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF0F172A))
        .padding(horizontal = 6.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      AssistChip(
        onClick = { viewModel.resetToDefaultManila() },
        label = { Text("City Hall", fontSize = 9.sp) }
      )
      AssistChip(
        onClick = {
          viewModel.locationEngine.landmarks.find { it.id == "rizal_park" }?.let {
            viewModel.jumpToLandmark(it)
          }
        },
        label = { Text("Luneta", fontSize = 9.sp) }
      )
      AssistChip(
        onClick = {
          viewModel.locationEngine.landmarks.find { it.id == "intramuros" }?.let {
            viewModel.jumpToLandmark(it)
          }
        },
        label = { Text("Intramuros", fontSize = 9.sp) }
      )
      AssistChip(
        onClick = {
          viewModel.locationEngine.landmarks.find { it.id == "baywalk" }?.let {
            viewModel.jumpToLandmark(it)
          }
        },
        label = { Text("Baywalk", fontSize = 9.sp) }
      )
    }
  }
}

/**
 * PAGASA Weather PH Screen inside the virtual phone
 */
@Composable
fun VirtualWeatherScreen(locationState: ManilaLocationState) {
  val report = remember { LocaleTimezoneEngine.getLatestWeatherReport() }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF0C4A6E), Color(0xFF075985), Color(0xFF0284C7))
        )
      )
      .padding(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Text(
      text = "DOST-PAGASA Weather",
      fontSize = 11.sp,
      color = ManilaGoldAccent,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = "Metro Manila, Philippines",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
    Text(
      text = "${report.temperatureC}°C",
      fontSize = 46.sp,
      fontWeight = FontWeight.ExtraBold,
      color = Color.White
    )
    Text(
      text = report.condition,
      fontSize = 11.sp,
      color = Color.White.copy(alpha = 0.9f)
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f)),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Feels Like", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
          Text("${report.feelsLikeC}°C (${report.heatIndexCategory})", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Humidity", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
          Text("${report.humidityPercent}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Coastal Wind", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
          Text("${report.windKmh} km/h ${report.windDirection}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Typhoon Alert", color = ManilaGoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("PAR Clear", color = Color(0xFF4ADE80), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Text(
      text = "Station: ${report.station}",
      fontSize = 8.5.sp,
      color = Color.White.copy(alpha = 0.6f),
      textAlign = TextAlign.Center
    )
  }
}

/**
 * Manila Public Transit & Jeepney Guide inside the virtual phone
 */
@Composable
fun VirtualCommuteScreen(
  locationState: ManilaLocationState,
  viewModel: ManilaSimViewModel,
) {
  val routes = listOf(
    Pair("Taft Ave Jeepney", "Manila City Hall ⇄ Baclaran (via Pedro Gil, UN Ave)"),
    Pair("LRT Line 1", "Roosevelt ⇄ Baclaran (Central Terminal beside City Hall)"),
    Pair("Pasig River Ferry", "Plaza Mexico (Intramuros) ⇄ Guadalupe (Makati)"),
    Pair("Quiapo Express", "Divisoria ⇄ Cubao (via Quezon Blvd & España)")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "Manila Commute & Jeepney Simulator",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = ManilaGoldAccent
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      shape = RoundedCornerShape(12.dp)
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Text("Active Simulated Route:", fontSize = 9.sp, color = Color(0xFF94A3B8))
        Text(locationState.simulationMode.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Current Speed: ${locationState.speedKmh} km/h", fontSize = 10.sp, color = Color(0xFF38BDF8))
      }
    }

    Text("Available Manila Transit Routes:", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)

    routes.forEach { (name, desc) ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            if (name.contains("Jeepney")) {
              viewModel.setSimulationMode(SimulationMode.TAFT_JEEPNEY)
            } else if (name.contains("Ferry")) {
              viewModel.setSimulationMode(SimulationMode.MANILA_BAY_CRUISE)
            } else {
              viewModel.setSimulationMode(SimulationMode.INTRAMUROS_HERITAGE)
            }
          },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Row(
          modifier = Modifier.padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.DirectionsBus, null, tint = ManilaGoldAccent, modifier = Modifier.size(18.dp))
          Column {
            Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = Color(0xFF94A3B8), fontSize = 8.5.sp)
          }
        }
      }
    }
  }
}

/**
 * PH Web Browser inside the virtual phone
 * Displays simulated web browser showing the Philippines exit IP, geolocation, and live web fetch test
 */
@Composable
fun VirtualBrowserScreen(
  viewModel: ManilaSimViewModel,
  phoneState: VirtualPhoneState,
) {
  val activeNode by viewModel.activeProxyNode.collectAsStateWithLifecycle()
  val isProxyEnabled by viewModel.isProxyEnabled.collectAsStateWithLifecycle()
  val geoDetails by viewModel.geoDetails.collectAsStateWithLifecycle()
  val latestResult by viewModel.latestFetchResult.collectAsStateWithLifecycle()
  val isFetching by viewModel.isFetching.collectAsStateWithLifecycle()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
  ) {
    // Browser address bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF1E293B))
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      IconButton(
        onClick = { viewModel.pressVirtualHome() },
        modifier = Modifier.size(24.dp)
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Home", tint = Color.White, modifier = Modifier.size(14.dp))
      }
      Surface(
        modifier = Modifier.weight(1f),
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Lock, null, tint = Color(0xFF4ADE80), modifier = Modifier.size(10.dp))
          Text(
            text = "https://ip-api.com/manila-ph",
            fontSize = 9.sp,
            color = Color(0xFFCBD5E1),
            fontFamily = FontFamily.Monospace,
            maxLines = 1
          )
        }
      }
      IconButton(
        onClick = { viewModel.executeWebFetch("https://httpbin.org/ip") },
        modifier = Modifier.size(24.dp)
      ) {
        Icon(Icons.Default.Refresh, "Reload", tint = Color.White, modifier = Modifier.size(14.dp))
      }
    }

    // Web Page Content
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "🇵🇭 Philippines IP Inspector",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ManilaGoldAccent
              )
              Surface(
                color = if (isProxyEnabled) Color(0xFF15803D) else Color(0xFF7F1D1D),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = if (isProxyEnabled) "PH PROXY ON" else "DIRECT",
                  color = Color.White,
                  fontSize = 7.5.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = "Exit IP: ${geoDetails.ip}",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "Location: ${geoDetails.city}, ${geoDetails.region}, Philippines",
              color = Color(0xFF94A3B8),
              fontSize = 9.5.sp
            )
            Text(
              text = "ISP: ${geoDetails.isp}",
              color = Color(0xFFCBD5E1),
              fontSize = 9.sp
            )
            Text(
              text = "Proxy Node: ${activeNode.name} (${activeNode.host}:${activeNode.port})",
              color = Color(0xFF38BDF8),
              fontSize = 8.5.sp
            )
          }
        }
      }

      item {
        Button(
          onClick = { viewModel.executeWebFetch("https://httpbin.org/ip") },
          enabled = !isFetching,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = ManilaNavyPrimary)
        ) {
          if (isFetching) {
            Text("Fetching via Manila Proxy...", fontSize = 10.sp)
          } else {
            Text("Test Fetch via PH Proxy", fontSize = 10.sp)
          }
        }
      }

      if (latestResult != null) {
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Response: HTTP ${latestResult?.statusCode} (${latestResult?.durationMs}ms)",
                color = Color(0xFF4ADE80),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = latestResult?.bodyExcerpt ?: "",
                color = Color.White,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }
  }
}

/**
 * System Settings Screen inside the virtual phone
 */
@Composable
fun VirtualSettingsScreen(
  locationState: ManilaLocationState,
  phoneState: VirtualPhoneState,
  viewModel: ManilaSimViewModel,
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    item {
      Text(
        text = "Virtual Android System Settings",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          SettingRow("System Locale", "en-PH (English - Philippines)", Icons.Default.Language)
          SettingRow("Time Zone", "Asia/Manila (PHT, UTC+08:00)", Icons.Default.Schedule)
          SettingRow("Default GPS Lat", "14.599500° N", Icons.Default.LocationOn)
          SettingRow("Default GPS Long", "120.984200° E", Icons.Default.NearMe)
          SettingRow("Carrier Network", phoneState.carrierName, Icons.Default.SignalCellular4Bar)
          SettingRow("Philippines Proxy", if (phoneState.isProxyActive) "Active (${phoneState.activeProxyNodeName})" else "Disabled", Icons.Default.VpnLock)
          SettingRow("Currency Tag", "PHP (₱ - Philippine Peso)", Icons.Default.CheckCircle)
        }
      }
    }

    item {
      Text(
        text = "Quick Mock Controls:",
        fontSize = 10.sp,
        color = ManilaGoldAccent,
        fontWeight = FontWeight.Bold
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Mock GPS Provider", color = Color.White, fontSize = 11.sp)
        Switch(
          checked = locationState.isMockProviderActive,
          onCheckedChange = { viewModel.toggleMockProvider(it) },
          colors = SwitchDefaults.colors(checkedThumbColor = ManilaGoldAccent, checkedTrackColor = ManilaNavyPrimary)
        )
      }
    }

    item {
      OutlinedButton(
        onClick = { viewModel.cycleVirtualCarrier() },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Toggle PH Carrier: ${phoneState.carrierName}", fontSize = 10.sp)
      }
    }
  }
}

@Composable
fun SettingRow(title: String, value: String, icon: ImageVector) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(icon, null, tint = ManilaGoldAccent, modifier = Modifier.size(13.dp))
      Text(title, color = Color(0xFFCBD5E1), fontSize = 10.sp)
    }
    Text(value, color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
  }
}

/**
 * Pull-down Notification Shade
 */
@Composable
fun VirtualNotificationShade(
  phoneState: VirtualPhoneState,
  locationState: ManilaLocationState,
  liveManilaTime: String,
  viewModel: ManilaSimViewModel,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF090D16).copy(alpha = 0.95f))
      .clickable { viewModel.toggleNotificationShade() }
      .padding(12.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header in Shade
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = liveManilaTime, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
          Text(text = LocaleTimezoneEngine.formatManilaDateFull(), color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
        IconButton(onClick = { viewModel.toggleNotificationShade() }) {
          Icon(Icons.Default.ArrowBack, "Close Shade", tint = Color.White, modifier = Modifier.size(18.dp))
        }
      }

      // Quick Toggles Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        QuickToggleIcon(
          label = "Mock GPS",
          active = locationState.isMockProviderActive,
          icon = Icons.Default.GpsFixed,
          onClick = { viewModel.toggleMockProvider(!locationState.isMockProviderActive) }
        )
        QuickToggleIcon(
          label = "Wi-Fi",
          active = phoneState.isWifiConnected,
          icon = Icons.Default.Wifi,
          onClick = { viewModel.toggleVirtualWifi() }
        )
        QuickToggleIcon(
          label = "5G Data",
          active = true,
          icon = Icons.Default.SignalCellular4Bar,
          onClick = { viewModel.cycleVirtualCarrier() }
        )
        QuickToggleIcon(
          label = "en-PH Time",
          active = true,
          icon = Icons.Default.Schedule,
          onClick = { viewModel.resetToDefaultManila() }
        )
      }

      // Notification cards
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.GpsFixed, null, tint = ManilaGoldAccent, modifier = Modifier.size(14.dp))
            Text("Android Location Service", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          Text(
            text = "Mock Location Active: Manila (${locationState.latitude}, ${locationState.longitude})",
            color = Color(0xFF38BDF8),
            fontSize = 9.sp
          )
        }
      }

      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Cloud, null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
            Text("PAGASA Advisory", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          Text(
            text = "Metro Manila: 31°C. Coastal breeze over Manila Bay. Normal weather condition.",
            color = Color(0xFFCBD5E1),
            fontSize = 9.sp
          )
        }
      }
    }
  }
}

@Composable
fun QuickToggleIcon(label: String, active: Boolean, icon: ImageVector, onClick: () -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(if (active) ManilaNavyPrimary else Color(0xFF334155)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (active) ManilaGoldAccent else Color.White.copy(alpha = 0.7f),
        modifier = Modifier.size(20.dp)
      )
    }
    Spacer(modifier = Modifier.height(3.dp))
    Text(text = label, color = Color.White, fontSize = 9.sp)
  }
}

/**
 * Side console displayed on wide screens next to the virtual phone
 */
@Composable
fun VirtualPhoneSideConsole(
  viewModel: ManilaSimViewModel,
  locationState: ManilaLocationState,
  phoneState: VirtualPhoneState,
  liveManilaSeconds: String,
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(start = 8.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Live Manila Device Status",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Surface(
              color = ManilaNavyPrimary,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "ONLINE",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Simulating a virtual Android client connected from Manila, National Capital Region, Philippines.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Hardcoded Coordinates",
            style = MaterialTheme.typography.labelLarge,
            color = ManilaGoldAccent,
            fontWeight = FontWeight.Bold
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            InfoChip(label = "LATITUDE", value = "14.599500° N", modifier = Modifier.weight(1f))
            InfoChip(label = "LONGITUDE", value = "120.984200° E", modifier = Modifier.weight(1f))
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            InfoChip(label = "LOCALE", value = "en-PH (Philippines)", modifier = Modifier.weight(1f))
            InfoChip(label = "TIMEZONE", value = "Asia/Manila (UTC+8)", modifier = Modifier.weight(1f))
          }
        }
      }
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Virtual Phone Screen Switcher",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            FilterChip(
              selected = phoneState.currentApp == VirtualApp.MAPS,
              onClick = { viewModel.openVirtualApp(VirtualApp.MAPS) },
              label = { Text("Maps", fontSize = 11.sp) }
            )
            FilterChip(
              selected = phoneState.currentApp == VirtualApp.WEATHER,
              onClick = { viewModel.openVirtualApp(VirtualApp.WEATHER) },
              label = { Text("PAGASA", fontSize = 11.sp) }
            )
            FilterChip(
              selected = phoneState.currentApp == VirtualApp.COMMUTE,
              onClick = { viewModel.openVirtualApp(VirtualApp.COMMUTE) },
              label = { Text("Jeepney", fontSize = 11.sp) }
            )
            FilterChip(
              selected = phoneState.currentApp == VirtualApp.BROWSER,
              onClick = { viewModel.openVirtualApp(VirtualApp.BROWSER) },
              label = { Text("PH Web", fontSize = 11.sp) }
            )
            FilterChip(
              selected = phoneState.currentApp == VirtualApp.HOME,
              onClick = { viewModel.pressVirtualHome() },
              label = { Text("Home", fontSize = 11.sp) }
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Wallpaper Theme",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            WallpaperMode.values().forEach { mode ->
              AssistChip(
                onClick = { viewModel.setWallpaper(mode) },
                label = { Text(mode.label, fontSize = 10.sp) }
              )
            }
          }
        }
      }
    }

    item {
      Button(
        onClick = { viewModel.resetToDefaultManila() },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("reset_manila_button"),
        colors = ButtonDefaults.buttonColors(containerColor = ManilaNavyPrimary)
      ) {
        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Reset GPS to Hardcoded Manila (14.5995, 120.9842)")
      }
    }
  }
}

/**
 * Quick control deck on compact mobile
 */
@Composable
fun VirtualPhoneQuickDeck(
  viewModel: ManilaSimViewModel,
  locationState: ManilaLocationState,
  phoneState: VirtualPhoneState,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(
        text = "Virtual Android Control Deck",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { viewModel.openVirtualApp(VirtualApp.MAPS) },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = ManilaNavyPrimary)
        ) {
          Text("Open Maps", fontSize = 11.sp)
        }
        OutlinedButton(
          onClick = { viewModel.resetToDefaultManila() },
          modifier = Modifier.weight(1f)
        ) {
          Text("Reset Anchor", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun InfoChip(label: String, value: String, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
  }
}
