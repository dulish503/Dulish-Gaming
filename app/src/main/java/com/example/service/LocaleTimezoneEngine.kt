package com.example.service

import android.content.Context
import com.example.model.ManilaConstants
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

/**
 * Handles Philippine (en-PH) system locale conventions and Asia/Manila (PHT) timezone operations.
 */
object LocaleTimezoneEngine {
  val PHILIPPINES_LOCALE: Locale = Locale.Builder()
    .setLanguage("en")
    .setRegion("PH")
    .build()

  val MANILA_ZONE_ID: ZoneId = ZoneId.of(ManilaConstants.TIMEZONE_ID)
  val MANILA_TIMEZONE: TimeZone = TimeZone.getTimeZone(ManilaConstants.TIMEZONE_ID)

  private val currencyFormatter = NumberFormat.getCurrencyInstance(PHILIPPINES_LOCALE)
  private val timeFormatter12h = DateTimeFormatter.ofPattern("h:mm a", PHILIPPINES_LOCALE)
  private val timeFormatterSeconds = DateTimeFormatter.ofPattern("h:mm:ss a", PHILIPPINES_LOCALE)
  private val dateFormatterFull = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", PHILIPPINES_LOCALE)
  private val dateFormatterShort = DateTimeFormatter.ofPattern("MMM d, yyyy", PHILIPPINES_LOCALE)

  /**
   * Initializes the process-wide JVM default locale and timezone to Philippines and Asia/Manila.
   */
  fun applySystemDefaults(context: Context) {
    try {
      Locale.setDefault(PHILIPPINES_LOCALE)
      TimeZone.setDefault(MANILA_TIMEZONE)
    } catch (_: Exception) {
      // Graceful fallback
    }
  }

  fun getManilaZonedNow(): ZonedDateTime {
    return ZonedDateTime.now(MANILA_ZONE_ID)
  }

  fun formatManilaTimeShort(zoned: ZonedDateTime = getManilaZonedNow()): String {
    return zoned.format(timeFormatter12h)
  }

  fun formatManilaTimeWithSeconds(zoned: ZonedDateTime = getManilaZonedNow()): String {
    return zoned.format(timeFormatterSeconds)
  }

  fun formatManilaDateFull(zoned: ZonedDateTime = getManilaZonedNow()): String {
    return zoned.format(dateFormatterFull)
  }

  fun formatManilaDateShort(zoned: ZonedDateTime = getManilaZonedNow()): String {
    return zoned.format(dateFormatterShort)
  }

  fun formatPhpCurrency(amount: Double): String {
    return try {
      currencyFormatter.format(amount)
    } catch (_: Exception) {
      "₱${String.format(PHILIPPINES_LOCALE, "%,.2f", amount)}"
    }
  }

  fun getUtcVsManilaComparison(): TimeComparison {
    val now = Instant.now()
    val manilaTime = now.atZone(MANILA_ZONE_ID)
    val utcTime = now.atZone(ZoneId.of("UTC"))
    val deviceSystemTime = now.atZone(ZoneId.systemDefault())

    return TimeComparison(
      manilaTimeString = manilaTime.format(timeFormatterSeconds),
      manilaDateString = manilaTime.format(dateFormatterFull),
      manilaZoneOffset = manilaTime.offset.toString(),
      utcTimeString = utcTime.format(timeFormatterSeconds),
      utcDateString = utcTime.format(dateFormatterShort),
      deviceSystemTimeString = deviceSystemTime.format(timeFormatterSeconds),
      deviceSystemTimezone = ZoneId.systemDefault().id,
    )
  }

  data class TimeComparison(
    val manilaTimeString: String,
    val manilaDateString: String,
    val manilaZoneOffset: String,
    val utcTimeString: String,
    val utcDateString: String,
    val deviceSystemTimeString: String,
    val deviceSystemTimezone: String,
  )

  data class PagasaWeatherReport(
    val station: String = "PAGASA Metro Manila Science Garden Station",
    val temperatureC: Int = 31,
    val feelsLikeC: Int = 36,
    val condition: String = "Partly Cloudy with Coastal Breeze",
    val humidityPercent: Int = 78,
    val windKmh: Int = 14,
    val windDirection: String = "WSW (Manila Bay)",
    val heatIndexCategory: String = "Extreme Caution",
    val barometricHpa: Double = 1011.8,
    val uvIndex: Int = 8,
    val typhoonAdvisory: String = "NO ACTIVE TROPICAL CYCLONE WITHIN PAR (Philippine Area of Responsibility)",
    val sunrisePht: String = "5:47 AM",
    val sunsetPht: String = "5:48 PM",
  )

  fun getLatestWeatherReport(): PagasaWeatherReport {
    return PagasaWeatherReport()
  }
}
