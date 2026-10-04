package com.example.model

/**
 * Core models for Manila, Philippines simulation:
 * - Hardcoded default GPS: Lat 14.5995, Long 120.9842
 * - System Locale: en-PH (English - Philippines)
 * - Timezone: Asia/Manila (PHT, UTC+8)
 */
object ManilaConstants {
  const val DEFAULT_LATITUDE = 14.5995
  const val DEFAULT_LONGITUDE = 120.9842
  const val DEFAULT_ALTITUDE = 16.2 // meters above sea level
  const val DEFAULT_ACCURACY = 3.5f // meters
  const val DEFAULT_GEOHASH = "wdw4bg"
  const val CITY_NAME = "Manila"
  const val PROVINCE = "Metro Manila (NCR)"
  const val COUNTRY = "Philippines"
  const val COUNTRY_CODE = "PH"
  const val TIMEZONE_ID = "Asia/Manila"
  const val LOCALE_TAG = "en-PH"
  const val CURRENCY_CODE = "PHP"
  const val CURRENCY_SYMBOL = "₱"
}

data class ManilaLocationState(
  val latitude: Double = ManilaConstants.DEFAULT_LATITUDE,
  val longitude: Double = ManilaConstants.DEFAULT_LONGITUDE,
  val altitude: Double = ManilaConstants.DEFAULT_ALTITUDE,
  val accuracy: Float = ManilaConstants.DEFAULT_ACCURACY,
  val speedKmh: Float = 0f,
  val bearing: Float = 45f,
  val timestamp: Long = System.currentTimeMillis(),
  val landmarkName: String = "Manila City Hall",
  val district: String = "Ermita, Manila",
  val isMockProviderActive: Boolean = true,
  val isRoutePlaying: Boolean = false,
  val simulationMode: SimulationMode = SimulationMode.STATIONARY,
  val satellitesVisible: Int = 14,
  val satellitesUsed: Int = 10,
  val hdop: Float = 0.9f,
)

data class ManilaLandmark(
  val id: String,
  val name: String,
  val category: String,
  val description: String,
  val latitude: Double,
  val longitude: Double,
  val altitude: Double = 16.0,
  val tag: String,
)

enum class SimulationMode(val label: String, val speedDescription: String, val baseSpeedKmh: Float) {
  STATIONARY("Locked at Manila Center", "0 km/h", 0f),
  BAYWALK_WALK("Roxas Boulevard Stroll", "4.5 km/h", 4.5f),
  INTRAMUROS_HERITAGE("Intramuros Heritage Walk", "3.8 km/h", 3.8f),
  TAFT_JEEPNEY("Taft Ave Jeepney Commute", "28.0 km/h", 28.0f),
  MANILA_BAY_CRUISE("Manila Bay Cruise", "14.0 km/h", 14.0f)
}

enum class VirtualApp(val title: String, val iconName: String) {
  HOME("Home", "home"),
  MAPS("Manila Maps", "map"),
  WEATHER("PAGASA Weather", "cloud"),
  COMMUTE("Jeepney & MRT", "directions_bus"),
  BROWSER("PH Web Browser", "public"),
  SETTINGS("Device Settings", "settings")
}

data class VirtualPhoneState(
  val currentApp: VirtualApp = VirtualApp.MAPS,
  val isScreenOn: Boolean = true,
  val isNotificationShadeOpen: Boolean = false,
  val carrierName: String = "Smart 5G PH",
  val signalBars: Int = 4,
  val batteryLevel: Int = 97,
  val isGpsActive: Boolean = true,
  val isWifiConnected: Boolean = true,
  val isProxyActive: Boolean = true,
  val activeProxyNodeName: String = "Manila PLDT Fiber Gateway",
  val wallpaperMode: WallpaperMode = WallpaperMode.BAY_SUNSET,
  val recentAppsHistory: List<VirtualApp> = listOf(VirtualApp.MAPS, VirtualApp.WEATHER, VirtualApp.HOME),
)

enum class WallpaperMode(val label: String) {
  BAY_SUNSET("Manila Bay Sunset"),
  AZURE_GRADIENT("Philippine Azure"),
  DARK_MINIMAL("Night Metro Manila"),
}

data class NmeaLogEntry(
  val rawText: String,
  val timestampText: String,
  val sentenceType: String,
)
