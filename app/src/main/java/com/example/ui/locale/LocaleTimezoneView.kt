package com.example.ui.locale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ManilaConstants
import com.example.service.LocaleTimezoneEngine
import com.example.ui.theme.ManilaGoldAccent
import com.example.ui.theme.ManilaNavyPrimary
import com.example.ui.theme.ManilaScarlet

@Composable
fun LocaleTimezoneScreen(
  liveManilaSeconds: String,
  modifier: Modifier = Modifier,
) {
  var testAmountInput by remember { mutableStateOf("1500.50") }
  val comparison = remember(liveManilaSeconds) { LocaleTimezoneEngine.getUtcVsManilaComparison() }
  val weather = remember { LocaleTimezoneEngine.getLatestWeatherReport() }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Hero Card: Philippine Standard Time & en-PH Locale
    item {
      ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(ManilaNavyPrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Schedule,
                  contentDescription = null,
                  tint = ManilaGoldAccent,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "Philippine Standard Time (PhST)",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Asia/Manila (UTC+08:00) • Republic Act No. 10535",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Surface(
              color = Color(0xFF15803D),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "PHT (UTC+8)",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          // Live Digital Clock
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF090D16),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = liveManilaSeconds,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = ManilaGoldAccent,
                letterSpacing = 1.sp
              )
              Text(
                text = comparison.manilaDateString,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Solar Day: Sunrise ${weather.sunrisePht} • Sunset ${weather.sunsetPht}",
                fontSize = 9.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          // System Locale Specs Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            LocaleBadge("TAG", ManilaConstants.LOCALE_TAG, modifier = Modifier.weight(1f))
            LocaleBadge("TIMEZONE", ManilaConstants.TIMEZONE_ID, modifier = Modifier.weight(1.3f))
            LocaleBadge("OFFSET", "+08:00", modifier = Modifier.weight(0.8f))
          }
        }
      }
    }

    // Time Comparison Grid (Manila vs UTC vs Device)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Time Synchronization Matrix",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          TimeRow(
            zoneName = "Manila, Philippines (PhST)",
            timeStr = comparison.manilaTimeString,
            dateStr = comparison.manilaDateString,
            badge = "Active System Target",
            badgeColor = ManilaGoldAccent
          )
          TimeRow(
            zoneName = "Coordinated Universal Time (UTC)",
            timeStr = comparison.utcTimeString,
            dateStr = comparison.utcDateString,
            badge = "UTC±00:00",
            badgeColor = Color(0xFF38BDF8)
          )
          TimeRow(
            zoneName = "Physical Host Device Clock",
            timeStr = comparison.deviceSystemTimeString,
            dateStr = comparison.deviceSystemTimezone,
            badge = "Host OS",
            badgeColor = Color(0xFF94A3B8)
          )
        }
      }
    }

    // Philippine Currency & Number Formatting (PHP / ₱)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.CurrencyExchange, null, tint = ManilaGoldAccent, modifier = Modifier.size(18.dp))
            Text(
              text = "en-PH Currency Formatter (Philippine Peso ₱)",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }

          OutlinedTextField(
            value = testAmountInput,
            onValueChange = { testAmountInput = it },
            label = { Text("Enter Numeric Amount (PHP)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )

          val parsedAmount = testAmountInput.toDoubleOrNull() ?: 0.0
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Formatted (Locale.Builder: en-PH)", fontSize = 9.sp, color = Color(0xFF94A3B8))
                Text(
                  text = LocaleTimezoneEngine.formatPhpCurrency(parsedAmount),
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF4ADE80),
                  fontFamily = FontFamily.Monospace
                )
              }
              Surface(
                color = ManilaNavyPrimary,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "ISO: PHP",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }
    }

    // Philippine Legal & Atmospheric Standards
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Philippine Standard Time Act (RA 10535)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Enacted to synchronize all official timepieces in the country with the Philippine Standard Time provided by PAGASA (DOST-Philippine Atmospheric, Geophysical and Astronomical Services Administration).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              modifier = Modifier.weight(1f),
              color = MaterialTheme.colorScheme.surface,
              shape = RoundedCornerShape(8.dp)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("Official Source", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("PAGASA Astronomical Observatory", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
              }
            }
            Surface(
              modifier = Modifier.weight(1f),
              color = MaterialTheme.colorScheme.surface,
              shape = RoundedCornerShape(8.dp)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("Daylight Saving", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("None (Permanent UTC+8)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun LocaleBadge(label: String, value: String, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(label, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(value, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun TimeRow(zoneName: String, timeStr: String, dateStr: String, badge: String, badgeColor: Color) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(10.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(zoneName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(dateStr, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
      Column(horizontalAlignment = Alignment.End) {
        Text(timeStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text(badge, fontSize = 8.sp, color = badgeColor, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}
