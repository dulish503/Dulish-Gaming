package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ManilaConstants
import com.example.service.LocaleTimezoneEngine
import com.example.service.MockLocationEngine
import com.example.service.PhilippinesProxyService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Manila Sim", appName)
  }

  @Test
  fun `verify Manila hardcoded constants`() {
    assertEquals(14.5995, ManilaConstants.DEFAULT_LATITUDE, 0.0001)
    assertEquals(120.9842, ManilaConstants.DEFAULT_LONGITUDE, 0.0001)
    assertEquals("Asia/Manila", ManilaConstants.TIMEZONE_ID)
    assertEquals("en-PH", ManilaConstants.LOCALE_TAG)
  }

  @Test
  fun `verify Philippine locale and currency formatting`() {
    val formattedPhp = LocaleTimezoneEngine.formatPhpCurrency(1500.50)
    assertTrue("Should format with Philippine Peso symbol", formattedPhp.contains("₱") || formattedPhp.contains("PHP"))

    val comparison = LocaleTimezoneEngine.getUtcVsManilaComparison()
    assertEquals("+08:00", comparison.manilaZoneOffset)
  }

  @Test
  fun `verify mock location engine NMEA generation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = MockLocationEngine(context)
    val sentences = engine.generateNmeaSentences(14.5995, 120.9842, 0f, 45f)
    assertTrue("Should generate at least 3 NMEA sentences", sentences.size >= 3)
    val rmc = sentences.first { it.sentenceType == "GPRMC" }
    assertTrue("RMC should contain GPRMC tag", rmc.rawText.contains("GPRMC"))
  }

  @Test
  fun `verify Philippines proxy service configuration`() {
    val proxyService = PhilippinesProxyService()
    val nodes = proxyService.defaultNodes
    assertTrue("Should have multiple curated Philippines proxy nodes", nodes.size >= 4)

    val primaryNode = nodes.first()
    assertEquals("Philippines", primaryNode.country)
    assertEquals("PH", primaryNode.countryCode)
    assertTrue("Node should be in Metro Manila", primaryNode.region.contains("Metro Manila"))

    val geo = proxyService.getNodeGeoDetails(primaryNode)
    assertEquals("Philippines", geo.country)
    assertEquals("Asia/Manila", geo.timezone)
    assertEquals(ManilaConstants.DEFAULT_LATITUDE, geo.latitude, 0.0001)
    assertEquals(ManilaConstants.DEFAULT_LONGITUDE, geo.longitude, 0.0001)

    // Test JVM proxy property management
    proxyService.applyJvmSystemProxy(primaryNode.host, primaryNode.port)
    assertEquals(primaryNode.host, System.getProperty("http.proxyHost"))
    assertEquals(primaryNode.port.toString(), System.getProperty("http.proxyPort"))
    assertEquals(primaryNode.host, System.getProperty("https.proxyHost"))
    assertEquals(primaryNode.port.toString(), System.getProperty("https.proxyPort"))

    proxyService.clearJvmSystemProxy()
    assertEquals(null, System.getProperty("http.proxyHost"))
    assertEquals(null, System.getProperty("https.proxyHost"))
  }
}
