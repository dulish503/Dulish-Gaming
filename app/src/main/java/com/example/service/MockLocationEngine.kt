package com.example.service

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import com.example.model.ManilaConstants
import com.example.model.ManilaLandmark
import com.example.model.ManilaLocationState
import com.example.model.NmeaLogEntry
import com.example.model.SimulationMode
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-precision Mock GPS engine centered on Manila, Philippines (14.5995, 120.9842).
 * Integrates with Android LocationManager test provider when permitted, and supplies
 * continuous simulated telemetry, NMEA sentences, and waypoint routes.
 */
class MockLocationEngine(private val context: Context) {

  private val locationManager: LocationManager? =
    context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

  val landmarks = listOf(
    ManilaLandmark(
      id = "city_hall",
      name = "Manila City Hall",
      category = "Civic Center",
      description = "Historic government seat featuring the iconic clock tower, hardcoded center of Manila simulation.",
      latitude = 14.5995,
      longitude = 120.9842,
      altitude = 16.2,
      tag = "Default Anchor"
    ),
    ManilaLandmark(
      id = "rizal_park",
      name = "Rizal Monument (Luneta)",
      category = "National Historic Monument",
      description = "Kilometer Zero of the Philippines, dedicated to Dr. José Rizal with ceremonial Marine honor guard.",
      latitude = 14.5831,
      longitude = 120.9794,
      altitude = 14.0,
      tag = "Km 0"
    ),
    ManilaLandmark(
      id = "intramuros",
      name = "Fort Santiago (Intramuros)",
      category = "Historic Walled City",
      description = "16th-century Spanish colonial citadel beside the Pasig River with cobblestone gates and stone bulwarks.",
      latitude = 14.5939,
      longitude = 120.9754,
      altitude = 15.1,
      tag = "Walled City"
    ),
    ManilaLandmark(
      id = "baywalk",
      name = "Manila Baywalk & Sunset Promenade",
      category = "Coastal Promenade",
      description = "Roxas Boulevard waterfront promenade famous worldwide for the golden Manila Bay sunset over Corregidor.",
      latitude = 14.5714,
      longitude = 120.9835,
      altitude = 4.5,
      tag = "Sunset Coast"
    ),
    ManilaLandmark(
      id = "binondo",
      name = "Binondo (Chinatown Arch)",
      category = "Cultural Heritage",
      description = "World's oldest Chinatown established in 1594, famous for authentic dim sum, hopia, and heritage alleys.",
      latitude = 14.6000,
      longitude = 120.9744,
      altitude = 12.0,
      tag = "Old Chinatown"
    ),
    ManilaLandmark(
      id = "natl_museum",
      name = "National Museum of Fine Arts",
      category = "Museum & Arts",
      description = "Home to Juan Luna's masterwork 'Spoliarium' and Philippine neoclassical architecture.",
      latitude = 14.5869,
      longitude = 120.9812,
      altitude = 16.0,
      tag = "Culture"
    ),
    ManilaLandmark(
      id = "malacanang",
      name = "Malacañang Palace",
      category = "Official Residence",
      description = "Official residence and principal workplace of the President of the Philippines along the Pasig River.",
      latitude = 14.5937,
      longitude = 120.9944,
      altitude = 18.0,
      tag = "Executive"
    ),
    ManilaLandmark(
      id = "moa_bay",
      name = "Mall of Asia & Bay Area",
      category = "Commercial Hub",
      description = "Vast reclamation bayside complex with seaside ferris wheel and sunset amphitheater.",
      latitude = 14.5352,
      longitude = 120.9822,
      altitude = 5.0,
      tag = "Bayside"
    ),
    ManilaLandmark(
      id = "bgc",
      name = "Bonifacio Global City (BGC)",
      category = "Financial District",
      description = "High-tech metropolitan district with high-street murals, modern tech headquarters, and green parks.",
      latitude = 14.5507,
      longitude = 121.0509,
      altitude = 29.0,
      tag = "Metro Hub"
    )
  )

  // Route definitions around Manila
  private val baywalkWaypoints = listOf(
    Pair(14.5831, 120.9794), // Luneta
    Pair(14.5772, 120.9810), // Quirino Grandstand
    Pair(14.5714, 120.9835), // Manila Yacht Club / Baywalk
    Pair(14.5620, 120.9858), // Malate Church / Baywalk South
    Pair(14.5535, 120.9880), // CCP Complex
  )

  private val intramurosWaypoints = listOf(
    Pair(14.5939, 120.9754), // Fort Santiago
    Pair(14.5912, 120.9738), // Manila Cathedral
    Pair(14.5891, 120.9752), // San Agustin Church
    Pair(14.5878, 120.9780), // Puerta Real
    Pair(14.5905, 120.9805), // National Museum
  )

  private val taftJeepneyWaypoints = listOf(
    Pair(14.5995, 120.9842), // Manila City Hall
    Pair(14.5910, 120.9840), // Taft Ave / P. Burgos
    Pair(14.5830, 120.9860), // United Nations Ave LRT
    Pair(14.5738, 120.9892), // Pedro Gil LRT
    Pair(14.5645, 120.9930), // Quirino Ave LRT
  )

  private var routeStepIndex = 0
  private var stepFraction = 0.0

  /**
   * Generates next position based on simulation mode and elapsed time.
   */
  fun calculateNextPosition(currentState: ManilaLocationState): ManilaLocationState {
    when (currentState.simulationMode) {
      SimulationMode.STATIONARY -> {
        // Locked pinpoint at Manila City Hall (or current selected landmark)
        return currentState.copy(
          speedKmh = 0f,
          timestamp = System.currentTimeMillis()
        )
      }
      SimulationMode.BAYWALK_WALK -> {
        return advanceAlongWaypoints(currentState, baywalkWaypoints, 4.5f, "Roxas Blvd Baywalk", "Malate, Manila")
      }
      SimulationMode.INTRAMUROS_HERITAGE -> {
        return advanceAlongWaypoints(currentState, intramurosWaypoints, 3.8f, "Intramuros Cobblestones", "Intramuros, Manila")
      }
      SimulationMode.TAFT_JEEPNEY -> {
        return advanceAlongWaypoints(currentState, taftJeepneyWaypoints, 28.0f, "Taft Avenue Corridor", "Ermita, Manila")
      }
      SimulationMode.MANILA_BAY_CRUISE -> {
        // Circular cruise in Manila Bay
        val centerLat = 14.5750
        val centerLng = 120.9650
        val radius = 0.008
        stepFraction += 0.04
        val lat = centerLat + radius * sin(stepFraction)
        val lng = centerLng + radius * cos(stepFraction)
        val bearing = ((stepFraction * 180 / Math.PI + 90) % 360).toFloat()
        return currentState.copy(
          latitude = lat,
          longitude = lng,
          speedKmh = 14.0f,
          bearing = bearing,
          altitude = 1.5,
          landmarkName = "Manila Bay Waters",
          district = "Manila Bay Off-Shore",
          timestamp = System.currentTimeMillis()
        )
      }
    }
  }

  private fun advanceAlongWaypoints(
    current: ManilaLocationState,
    waypoints: List<Pair<Double, Double>>,
    speedKmh: Float,
    routeLabel: String,
    districtLabel: String,
  ): ManilaLocationState {
    if (waypoints.size < 2) return current

    stepFraction += 0.05
    if (stepFraction >= 1.0) {
      stepFraction = 0.0
      routeStepIndex = (routeStepIndex + 1) % (waypoints.size - 1)
    }

    val p1 = waypoints[routeStepIndex]
    val p2 = waypoints[routeStepIndex + 1]

    val lat = p1.first + (p2.first - p1.first) * stepFraction
    val lng = p1.second + (p2.second - p1.second) * stepFraction

    val dLat = p2.first - p1.first
    val dLng = p2.second - p1.second
    val angle = Math.toDegrees(Math.atan2(dLng, dLat)).toFloat()
    val bearing = (angle + 360f) % 360f

    return current.copy(
      latitude = lat,
      longitude = lng,
      speedKmh = speedKmh,
      bearing = bearing,
      landmarkName = routeLabel,
      district = districtLabel,
      timestamp = System.currentTimeMillis()
    )
  }

  /**
   * Attempts to push the mock location to Android's system LocationManager test provider.
   * If not granted developer mock permissions, catches gracefully.
   */
  fun pushToAndroidSystem(lat: Double, lng: Double, alt: Double, accuracy: Float): Result<String> {
    val lm = locationManager ?: return Result.failure(IllegalStateException("LocationManager unavailable"))
    val provider = LocationManager.GPS_PROVIDER

    return try {
      // Setup test provider if not already registered
      try {
        lm.addTestProvider(
          provider,
          false,
          false,
          false,
          false,
          true,
          true,
          true,
          android.location.Criteria.POWER_LOW,
          android.location.Criteria.ACCURACY_FINE
        )
        lm.setTestProviderEnabled(provider, true)
      } catch (_: SecurityException) {
        // Likely requires user to select this app in Android Developer Options -> Select Mock Location App
        return Result.failure(
          SecurityException("Android requires selecting 'Manila Sim' in Developer Options -> Select Mock Location App.")
        )
      } catch (_: IllegalArgumentException) {
        // Provider already exists
      }

      val mockLocation = Location(provider).apply {
        latitude = lat
        longitude = lng
        altitude = alt
        this.accuracy = accuracy
        time = System.currentTimeMillis()
        elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          bearingAccuracyDegrees = 0.5f
          verticalAccuracyMeters = 1.0f
          speedAccuracyMetersPerSecond = 0.1f
        }
      }

      lm.setTestProviderLocation(provider, mockLocation)
      Result.success("Mock GPS location pushed to system: $lat, $lng")
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Generates genuine NMEA 0183 sentences ($GPRMC, $GPGGA) for Manila coordinates.
   */
  fun generateNmeaSentences(lat: Double, lng: Double, speedKmh: Float, bearing: Float): List<NmeaLogEntry> {
    val now = ZonedDateTime.now(LocaleTimezoneEngine.MANILA_ZONE_ID)
    val timeStr = now.format(DateTimeFormatter.ofPattern("HHmmss.ss", Locale.US))
    val dateStr = now.format(DateTimeFormatter.ofPattern("ddMMyy", Locale.US))

    // Convert lat to DDMM.MMMM format
    val latDeg = lat.toInt()
    val latMin = (lat - latDeg) * 60.0
    val latStr = String.format(Locale.US, "%02d%07.4f", latDeg, latMin)

    // Convert lng to DDDMM.MMMM format
    val lngDeg = lng.toInt()
    val lngMin = (lng - lngDeg) * 60.0
    val lngStr = String.format(Locale.US, "%03d%07.4f", lngDeg, lngMin)

    val speedKnots = speedKmh * 0.539957f
    val speedStr = String.format(Locale.US, "%05.1f", speedKnots)
    val bearingStr = String.format(Locale.US, "%05.1f", bearing)

    // GPRMC: Recommended Minimum Specific GPS/TRANSIT Data
    val rmcPayload = "GPRMC,$timeStr,A,$latStr,N,$lngStr,E,$speedStr,$bearingStr,$dateStr,,,A"
    val rmcSentence = "\$$rmcPayload*${calculateChecksum(rmcPayload)}"

    // GPGGA: Global Positioning System Fix Data
    val ggaPayload = "GPGGA,$timeStr,$latStr,N,$lngStr,E,1,10,0.9,16.2,M,14.5,M,,"
    val ggaSentence = "\$$ggaPayload*${calculateChecksum(ggaPayload)}"

    // GPGSV: Satellites in View
    val gsvPayload = "GPGSV,3,1,12,01,65,045,46,03,42,120,44,06,78,210,48,11,35,310,41"
    val gsvSentence = "\$$gsvPayload*${calculateChecksum(gsvPayload)}"

    val timestampText = now.format(DateTimeFormatter.ofPattern("HH:mm:ss.S", Locale.US))

    return listOf(
      NmeaLogEntry(rmcSentence, timestampText, "GPRMC"),
      NmeaLogEntry(ggaSentence, timestampText, "GPGGA"),
      NmeaLogEntry(gsvSentence, timestampText, "GPGSV")
    )
  }

  private fun calculateChecksum(data: String): String {
    var checksum = 0
    for (ch in data) {
      checksum = checksum xor ch.code
    }
    return String.format(Locale.US, "%02X", checksum)
  }
}
