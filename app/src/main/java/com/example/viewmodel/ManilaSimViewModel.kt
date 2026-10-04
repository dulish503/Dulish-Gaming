package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.IpGeoDetails
import com.example.model.ManilaConstants
import com.example.model.ManilaLandmark
import com.example.model.ManilaLocationState
import com.example.model.NmeaLogEntry
import com.example.model.PhilippinesProxyNode
import com.example.model.ProxyFetchResult
import com.example.model.ProxyProtocol
import com.example.model.ProxyRoutingMode
import com.example.model.SimulationMode
import com.example.model.VirtualApp
import com.example.model.VirtualPhoneState
import com.example.model.WallpaperMode
import com.example.service.LocaleTimezoneEngine
import com.example.service.MockLocationEngine
import com.example.service.PhilippinesProxyService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class MainNavigationTab(val title: String, val iconTag: String) {
  VIRTUAL_PHONE("Virtual Phone", "smartphone"),
  GPS_TELEMETRY("Mock GPS", "gps_fixed"),
  PH_PROXY("PH Proxy & IP", "vpn_lock"),
  LOCALE_TIMEZONE("en-PH & PHT", "schedule"),
  LANDMARKS("Manila POIs", "place")
}

class ManilaSimViewModel(application: Application) : AndroidViewModel(application) {

  val locationEngine = MockLocationEngine(application)
  val proxyService = PhilippinesProxyService()

  private val _selectedTab = MutableStateFlow(MainNavigationTab.VIRTUAL_PHONE)
  val selectedTab: StateFlow<MainNavigationTab> = _selectedTab.asStateFlow()

  private val _locationState = MutableStateFlow(ManilaLocationState())
  val locationState: StateFlow<ManilaLocationState> = _locationState.asStateFlow()

  private val _virtualPhoneState = MutableStateFlow(VirtualPhoneState())
  val virtualPhoneState: StateFlow<VirtualPhoneState> = _virtualPhoneState.asStateFlow()

  private val _nmeaLogs = MutableStateFlow<List<NmeaLogEntry>>(emptyList())
  val nmeaLogs: StateFlow<List<NmeaLogEntry>> = _nmeaLogs.asStateFlow()

  private val _systemMockMessage = MutableStateFlow<String?>("Mock GPS initialized to Manila (14.5995, 120.9842)")
  val systemMockMessage: StateFlow<String?> = _systemMockMessage.asStateFlow()

  private val _liveManilaTime = MutableStateFlow(LocaleTimezoneEngine.formatManilaTimeShort())
  val liveManilaTime: StateFlow<String> = _liveManilaTime.asStateFlow()

  private val _liveManilaSeconds = MutableStateFlow(LocaleTimezoneEngine.formatManilaTimeWithSeconds())
  val liveManilaSeconds: StateFlow<String> = _liveManilaSeconds.asStateFlow()

  // Proxy state
  private val _proxyNodes = MutableStateFlow(proxyService.defaultNodes)
  val proxyNodes: StateFlow<List<PhilippinesProxyNode>> = _proxyNodes.asStateFlow()

  private val _activeProxyNode = MutableStateFlow(proxyService.defaultNodes.first())
  val activeProxyNode: StateFlow<PhilippinesProxyNode> = _activeProxyNode.asStateFlow()

  private val _isProxyEnabled = MutableStateFlow(true)
  val isProxyEnabled: StateFlow<Boolean> = _isProxyEnabled.asStateFlow()

  private val _routingMode = MutableStateFlow(ProxyRoutingMode.AUTO_FALLBACK)
  val routingMode: StateFlow<ProxyRoutingMode> = _routingMode.asStateFlow()

  private val _latestFetchResult = MutableStateFlow<ProxyFetchResult?>(null)
  val latestFetchResult: StateFlow<ProxyFetchResult?> = _latestFetchResult.asStateFlow()

  private val _isFetching = MutableStateFlow(false)
  val isFetching: StateFlow<Boolean> = _isFetching.asStateFlow()

  private val _fetchHistory = MutableStateFlow<List<ProxyFetchResult>>(emptyList())
  val fetchHistory: StateFlow<List<ProxyFetchResult>> = _fetchHistory.asStateFlow()

  private val _geoDetails = MutableStateFlow(proxyService.getNodeGeoDetails(proxyService.defaultNodes.first()))
  val geoDetails: StateFlow<IpGeoDetails> = _geoDetails.asStateFlow()

  init {
    // Apply en-PH and Asia/Manila to process defaults
    LocaleTimezoneEngine.applySystemDefaults(application)

    // Apply default Philippines proxy to JVM
    val defaultNode = proxyService.defaultNodes.first()
    proxyService.applyJvmSystemProxy(defaultNode.host, defaultNode.port)

    // Pre-populate an initial verified fetch from Philippines IP
    executeWebFetch("https://httpbin.org/ip")

    // Start 1-second ticker for live Manila time and NMEA stream
    viewModelScope.launch {
      while (isActive) {
        val nowTime = LocaleTimezoneEngine.formatManilaTimeShort()
        val nowSeconds = LocaleTimezoneEngine.formatManilaTimeWithSeconds()
        _liveManilaTime.value = nowTime
        _liveManilaSeconds.value = nowSeconds

        val currentLoc = _locationState.value
        // If route or simulation is moving, calculate position
        val nextLoc = if (currentLoc.isRoutePlaying || currentLoc.simulationMode != SimulationMode.STATIONARY) {
          locationEngine.calculateNextPosition(currentLoc)
        } else {
          currentLoc.copy(timestamp = System.currentTimeMillis())
        }
        _locationState.value = nextLoc

        // Generate NMEA sentences
        val newNmea = locationEngine.generateNmeaSentences(
          nextLoc.latitude,
          nextLoc.longitude,
          nextLoc.speedKmh,
          nextLoc.bearing
        )
        _nmeaLogs.update { list ->
          (newNmea + list).take(40)
        }

        // Try pushing to system provider if mock active
        if (nextLoc.isMockProviderActive) {
          locationEngine.pushToAndroidSystem(
            nextLoc.latitude,
            nextLoc.longitude,
            nextLoc.altitude,
            nextLoc.accuracy
          )
        }

        delay(1000L)
      }
    }
  }

  fun selectTab(tab: MainNavigationTab) {
    _selectedTab.value = tab
  }

  fun setSimulationMode(mode: SimulationMode) {
    _locationState.update { current ->
      if (mode == SimulationMode.STATIONARY) {
        current.copy(
          simulationMode = mode,
          latitude = ManilaConstants.DEFAULT_LATITUDE,
          longitude = ManilaConstants.DEFAULT_LONGITUDE,
          speedKmh = 0f,
          landmarkName = "Manila City Hall",
          district = "Ermita, Manila"
        )
      } else {
        current.copy(
          simulationMode = mode,
          isRoutePlaying = true,
          speedKmh = mode.baseSpeedKmh
        )
      }
    }
  }

  fun jumpToLandmark(landmark: ManilaLandmark) {
    _locationState.update { current ->
      current.copy(
        latitude = landmark.latitude,
        longitude = landmark.longitude,
        altitude = landmark.altitude,
        landmarkName = landmark.name,
        district = landmark.category,
        speedKmh = 0f,
        simulationMode = SimulationMode.STATIONARY,
        timestamp = System.currentTimeMillis()
      )
    }
    _systemMockMessage.value = "GPS position moved to ${landmark.name} (${landmark.latitude}, ${landmark.longitude})"
  }

  fun resetToDefaultManila() {
    _locationState.update {
      ManilaLocationState(
        latitude = ManilaConstants.DEFAULT_LATITUDE,
        longitude = ManilaConstants.DEFAULT_LONGITUDE,
        landmarkName = "Manila City Hall",
        district = "Ermita, Manila",
        simulationMode = SimulationMode.STATIONARY,
        speedKmh = 0f,
        bearing = 45f
      )
    }
    _systemMockMessage.value = "GPS reset to hardcoded Manila (14.5995, 120.9842)"
  }

  fun toggleMockProvider(active: Boolean) {
    _locationState.update { it.copy(isMockProviderActive = active) }
    _virtualPhoneState.update { it.copy(isGpsActive = active) }
    _systemMockMessage.value = if (active) {
      "Mock GPS Provider enabled (Manila: 14.5995, 120.9842)"
    } else {
      "Mock GPS Provider paused"
    }
  }

  fun pushMockLocationExplicitly() {
    val loc = _locationState.value
    val res = locationEngine.pushToAndroidSystem(loc.latitude, loc.longitude, loc.altitude, loc.accuracy)
    res.fold(
      onSuccess = { msg -> _systemMockMessage.value = msg },
      onFailure = { err -> _systemMockMessage.value = err.message ?: "Failed to set mock location" }
    )
  }

  // --- Proxy Operations ---
  fun selectProxyNode(node: PhilippinesProxyNode) {
    _activeProxyNode.value = node
    _geoDetails.value = proxyService.getNodeGeoDetails(node)
    _virtualPhoneState.update {
      it.copy(activeProxyNodeName = node.name)
    }
    if (_isProxyEnabled.value) {
      proxyService.applyJvmSystemProxy(node.host, node.port)
      _systemMockMessage.value = "Routing through ${node.name} (${node.city}, Asia/Manila)"
    }
  }

  fun toggleProxy(enabled: Boolean) {
    _isProxyEnabled.value = enabled
    _virtualPhoneState.update { it.copy(isProxyActive = enabled) }
    if (enabled) {
      val node = _activeProxyNode.value
      proxyService.applyJvmSystemProxy(node.host, node.port)
      _systemMockMessage.value = "Philippines Proxy enabled (${node.name})"
    } else {
      proxyService.clearJvmSystemProxy()
      _systemMockMessage.value = "Proxy routing disabled"
    }
  }

  fun setRoutingMode(mode: ProxyRoutingMode) {
    _routingMode.value = mode
  }

  fun executeWebFetch(targetUrl: String) {
    val url = if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")) {
      targetUrl
    } else {
      "https://$targetUrl"
    }

    viewModelScope.launch {
      _isFetching.value = true
      try {
        val result = proxyService.executeFetch(
          url = url,
          node = _activeProxyNode.value,
          routingMode = _routingMode.value
        )
        _latestFetchResult.value = result
        _fetchHistory.update { (listOf(result) + it).take(15) }
      } finally {
        _isFetching.value = false
      }
    }
  }

  fun addCustomProxy(
    name: String,
    host: String,
    port: Int,
    protocol: ProxyProtocol,
    isp: String,
    city: String
  ) {
    val newNode = PhilippinesProxyNode(
      id = "custom_${System.currentTimeMillis()}",
      name = name.ifEmpty { "Custom Manila Proxy" },
      host = host,
      port = port,
      protocol = protocol,
      city = city.ifEmpty { "Manila" },
      region = "Metro Manila",
      isp = isp.ifEmpty { "Philippine ISP" },
      simulatedIp = host,
      latencyMs = 48,
      isVerified = true,
      anonymLevel = "Custom Node"
    )
    _proxyNodes.update { listOf(newNode) + it }
    selectProxyNode(newNode)
  }

  // Virtual phone interactions
  fun openVirtualApp(app: VirtualApp) {
    _virtualPhoneState.update { current ->
      val history = (listOf(app) + current.recentAppsHistory).distinct().take(5)
      current.copy(
        currentApp = app,
        isNotificationShadeOpen = false,
        recentAppsHistory = history
      )
    }
  }

  fun pressVirtualHome() {
    _virtualPhoneState.update {
      it.copy(
        currentApp = VirtualApp.HOME,
        isNotificationShadeOpen = false
      )
    }
  }

  fun pressVirtualBack() {
    _virtualPhoneState.update { current ->
      if (current.isNotificationShadeOpen) {
        current.copy(isNotificationShadeOpen = false)
      } else if (current.currentApp != VirtualApp.HOME) {
        current.copy(currentApp = VirtualApp.HOME)
      } else {
        current
      }
    }
  }

  fun toggleNotificationShade() {
    _virtualPhoneState.update { it.copy(isNotificationShadeOpen = !it.isNotificationShadeOpen) }
  }

  fun setWallpaper(wallpaper: WallpaperMode) {
    _virtualPhoneState.update { it.copy(wallpaperMode = wallpaper) }
  }

  fun toggleVirtualWifi() {
    _virtualPhoneState.update { it.copy(isWifiConnected = !it.isWifiConnected) }
  }

  fun cycleVirtualCarrier() {
    _virtualPhoneState.update { current ->
      val newCarrier = when (current.carrierName) {
        "Smart 5G PH" -> "Globe 5G PH"
        "Globe 5G PH" -> "DITO Telecommunity"
        else -> "Smart 5G PH"
      }
      current.copy(carrierName = newCarrier)
    }
  }

  fun clearNmeaLogs() {
    _nmeaLogs.value = emptyList()
  }

  fun dismissSystemMessage() {
    _systemMockMessage.value = null
  }
}
