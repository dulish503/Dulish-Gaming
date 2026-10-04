package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.VirtualApp
import com.example.ui.landmarks.ManilaLandmarksScreen
import com.example.ui.locale.LocaleTimezoneScreen
import com.example.ui.proxy.PhilippinesProxyScreen
import com.example.ui.telemetry.GpsTelemetryScreen
import com.example.ui.theme.ManilaGoldAccent
import com.example.ui.theme.ManilaNavyDark
import com.example.ui.theme.ManilaNavyPrimary
import com.example.ui.theme.ManilaScarlet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.virtualphone.VirtualPhoneScreen
import com.example.viewmodel.MainNavigationTab
import com.example.viewmodel.ManilaSimViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

  private val viewModel: ManilaSimViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        ManilaSimApp(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManilaSimApp(viewModel: ManilaSimViewModel) {
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val locationState by viewModel.locationState.collectAsStateWithLifecycle()
  val phoneState by viewModel.virtualPhoneState.collectAsStateWithLifecycle()
  val nmeaLogs by viewModel.nmeaLogs.collectAsStateWithLifecycle()
  val systemMessage by viewModel.systemMockMessage.collectAsStateWithLifecycle()
  val liveManilaTime by viewModel.liveManilaTime.collectAsStateWithLifecycle()
  val liveManilaSeconds by viewModel.liveManilaSeconds.collectAsStateWithLifecycle()

  // Proxy state
  val activeProxyNode by viewModel.activeProxyNode.collectAsStateWithLifecycle()
  val proxyNodes by viewModel.proxyNodes.collectAsStateWithLifecycle()
  val isProxyEnabled by viewModel.isProxyEnabled.collectAsStateWithLifecycle()
  val routingMode by viewModel.routingMode.collectAsStateWithLifecycle()
  val latestFetchResult by viewModel.latestFetchResult.collectAsStateWithLifecycle()
  val isFetching by viewModel.isFetching.collectAsStateWithLifecycle()
  val geoDetails by viewModel.geoDetails.collectAsStateWithLifecycle()

  // Handle hardware back press
  BackHandler(enabled = selectedTab != MainNavigationTab.VIRTUAL_PHONE || phoneState.currentApp != VirtualApp.HOME) {
    if (selectedTab != MainNavigationTab.VIRTUAL_PHONE) {
      viewModel.selectTab(MainNavigationTab.VIRTUAL_PHONE)
    } else {
      viewModel.pressVirtualBack()
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val isTablet = maxWidth > 840.dp

    if (isTablet) {
      // Tablet/Desktop Layout with Side Navigation Rail
      Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
          modifier = Modifier.fillMaxHeight(),
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          header = {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(vertical = 12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .background(ManilaNavyPrimary, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.GpsFixed,
                  contentDescription = null,
                  tint = ManilaGoldAccent,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Manila Sim",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        ) {
          MainNavigationTab.values().forEach { tab ->
            NavigationRailItem(
              selected = selectedTab == tab,
              onClick = { viewModel.selectTab(tab) },
              icon = { Icon(getTabIcon(tab), contentDescription = tab.title) },
              label = { Text(tab.title, fontSize = 10.sp) },
              colors = NavigationRailItemDefaults.colors(
                selectedIconColor = ManilaGoldAccent,
                indicatorColor = ManilaNavyPrimary
              ),
              modifier = Modifier.testTag("nav_rail_${tab.name.lowercase()}")
            )
          }
        }

        Scaffold(
          topBar = {
            ManilaTopBar(
              locationStateLat = locationState.latitude,
              locationStateLng = locationState.longitude,
              liveManilaTime = liveManilaTime,
              isProxyActive = isProxyEnabled,
              onReset = { viewModel.resetToDefaultManila() }
            )
          }
        ) { paddingValues ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues)
          ) {
            when (selectedTab) {
              MainNavigationTab.VIRTUAL_PHONE -> VirtualPhoneScreen(
                viewModel = viewModel,
                locationState = locationState,
                phoneState = phoneState,
                liveManilaTime = liveManilaTime,
                liveManilaSeconds = liveManilaSeconds
              )
              MainNavigationTab.GPS_TELEMETRY -> GpsTelemetryScreen(
                viewModel = viewModel,
                locationState = locationState,
                nmeaLogs = nmeaLogs,
                systemMessage = systemMessage
              )
              MainNavigationTab.PH_PROXY -> PhilippinesProxyScreen(
                viewModel = viewModel,
                activeNode = activeProxyNode,
                proxyNodes = proxyNodes,
                isProxyEnabled = isProxyEnabled,
                routingMode = routingMode,
                latestResult = latestFetchResult,
                isFetching = isFetching,
                geoDetails = geoDetails
              )
              MainNavigationTab.LOCALE_TIMEZONE -> LocaleTimezoneScreen(
                liveManilaSeconds = liveManilaSeconds
              )
              MainNavigationTab.LANDMARKS -> ManilaLandmarksScreen(
                viewModel = viewModel,
                locationState = locationState
              )
            }
          }
        }
      }
    } else {
      // Mobile / Compact Layout with Bottom Navigation Bar
      Scaffold(
        topBar = {
          ManilaTopBar(
            locationStateLat = locationState.latitude,
            locationStateLng = locationState.longitude,
            liveManilaTime = liveManilaTime,
            isProxyActive = isProxyEnabled,
            onReset = { viewModel.resetToDefaultManila() }
          )
        },
        bottomBar = {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.testTag("bottom_nav_bar")
          ) {
            MainNavigationTab.values().forEach { tab ->
              NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { viewModel.selectTab(tab) },
                icon = { Icon(getTabIcon(tab), contentDescription = tab.title) },
                label = { Text(tab.title, fontSize = 9.5.sp, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = ManilaGoldAccent,
                  indicatorColor = ManilaNavyPrimary
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
              )
            }
          }
        }
      ) { paddingValues ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
          when (selectedTab) {
            MainNavigationTab.VIRTUAL_PHONE -> VirtualPhoneScreen(
              viewModel = viewModel,
              locationState = locationState,
              phoneState = phoneState,
              liveManilaTime = liveManilaTime,
              liveManilaSeconds = liveManilaSeconds
            )
            MainNavigationTab.GPS_TELEMETRY -> GpsTelemetryScreen(
              viewModel = viewModel,
              locationState = locationState,
              nmeaLogs = nmeaLogs,
              systemMessage = systemMessage
            )
            MainNavigationTab.PH_PROXY -> PhilippinesProxyScreen(
              viewModel = viewModel,
              activeNode = activeProxyNode,
              proxyNodes = proxyNodes,
              isProxyEnabled = isProxyEnabled,
              routingMode = routingMode,
              latestResult = latestFetchResult,
              isFetching = isFetching,
              geoDetails = geoDetails
            )
            MainNavigationTab.LOCALE_TIMEZONE -> LocaleTimezoneScreen(
              liveManilaSeconds = liveManilaSeconds
            )
            MainNavigationTab.LANDMARKS -> ManilaLandmarksScreen(
              viewModel = viewModel,
              locationState = locationState
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManilaTopBar(
  locationStateLat: Double,
  locationStateLng: Double,
  liveManilaTime: String,
  isProxyActive: Boolean,
  onReset: () -> Unit,
) {
  TopAppBar(
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Manila Sim",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              color = ManilaNavyDark,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "en-PH",
                color = ManilaGoldAccent,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
            if (isProxyActive) {
              Surface(
                color = Color(0xFF15803D),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "PH PROXY",
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }
          }
          Text(
            text = String.format(Locale.US, "GPS: %.4f° N, %.4f° E • %s PHT", locationStateLat, locationStateLng, liveManilaTime),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    },
    actions = {
      IconButton(
        onClick = onReset,
        modifier = Modifier.testTag("app_bar_reset_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reset to Manila Center",
          tint = MaterialTheme.colorScheme.primary
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  )
}

fun getTabIcon(tab: MainNavigationTab): ImageVector {
  return when (tab) {
    MainNavigationTab.VIRTUAL_PHONE -> Icons.Default.Smartphone
    MainNavigationTab.GPS_TELEMETRY -> Icons.Default.GpsFixed
    MainNavigationTab.PH_PROXY -> Icons.Default.VpnLock
    MainNavigationTab.LOCALE_TIMEZONE -> Icons.Default.Schedule
    MainNavigationTab.LANDMARKS -> Icons.Default.Place
  }
}
