package com.example.ui.telemetry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ManilaConstants
import com.example.model.ManilaLocationState
import com.example.model.NmeaLogEntry
import com.example.model.SimulationMode
import com.example.ui.theme.ManilaEmerald
import com.example.ui.theme.ManilaGoldAccent
import com.example.ui.theme.ManilaNavyPrimary
import com.example.ui.theme.ManilaScarlet
import com.example.viewmodel.ManilaSimViewModel
import java.util.Locale

@Composable
fun GpsTelemetryScreen(
  viewModel: ManilaSimViewModel,
  locationState: ManilaLocationState,
  nmeaLogs: List<NmeaLogEntry>,
  systemMessage: String?,
  modifier: Modifier = Modifier,
) {
  val clipboardManager = LocalClipboardManager.current
  var isAdbGuideOpen by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Hero Card: Primary Hardcoded Manila GPS State
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
                  imageVector = Icons.Default.GpsFixed,
                  contentDescription = null,
                  tint = ManilaGoldAccent,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "Hardcoded Manila GPS Engine",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${locationState.landmarkName} • ${locationState.district}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Surface(
              color = if (locationState.isMockProviderActive) Color(0xFF15803D) else Color(0xFFB91C1C),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = if (locationState.isMockProviderActive) "BROADCASTING" else "PAUSED",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          // Main Coordinates Display
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CoordinateCard(
              title = "LATITUDE",
              value = String.format(Locale.US, "%.6f° N", locationState.latitude),
              target = "Default: 14.599500°",
              modifier = Modifier.weight(1f)
            )
            CoordinateCard(
              title = "LONGITUDE",
              value = String.format(Locale.US, "%.6f° E", locationState.longitude),
              target = "Default: 120.984200°",
              modifier = Modifier.weight(1f)
            )
          }

          // Telemetry Quad Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TelemetryMini(
              label = "Speed",
              value = "${String.format(Locale.US, "%.1f", locationState.speedKmh)} km/h",
              icon = Icons.Default.Speed,
              modifier = Modifier.weight(1f)
            )
            TelemetryMini(
              label = "Altitude",
              value = "${locationState.altitude} m",
              icon = Icons.Default.Explore,
              modifier = Modifier.weight(1f)
            )
            TelemetryMini(
              label = "Accuracy",
              value = "±${locationState.accuracy} m",
              icon = Icons.Default.LocationOn,
              modifier = Modifier.weight(1f)
            )
            TelemetryMini(
              label = "Satellites",
              value = "${locationState.satellitesUsed}/${locationState.satellitesVisible}",
              icon = Icons.Default.Satellite,
              modifier = Modifier.weight(1f)
            )
          }

          // Reset Button
          Button(
            onClick = { viewModel.resetToDefaultManila() },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reset_coordinates_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ManilaNavyPrimary)
          ) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset to Hardcoded Manila (14.5995, 120.9842)")
          }
        }
      }
    }

    // Simulation Mode Selector
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
            text = "Movement Simulation Mode",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Simulate traveling across historic corridors and bayside roads of Manila:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SimulationMode.values().forEach { mode ->
              val isSelected = locationState.simulationMode == mode
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { viewModel.setSimulationMode(mode) }
                  .border(
                    width = if (isSelected) 1.5.dp else 0.dp,
                    color = if (isSelected) ManilaGoldAccent else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                  ),
                color = if (isSelected) ManilaNavyPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(
                      imageVector = if (mode == SimulationMode.STATIONARY) Icons.Default.LocationOn else Icons.Default.DirectionsRun,
                      contentDescription = null,
                      tint = if (isSelected) ManilaGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = mode.label,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                  Text(
                    text = mode.speedDescription,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) ManilaGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }

    // System Mock Provider Integration Status & ADB Command Box
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Android System Test Provider",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Pushes mock location to Android LocationManager",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = locationState.isMockProviderActive,
              onCheckedChange = { viewModel.toggleMockProvider(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = ManilaGoldAccent,
                checkedTrackColor = ManilaNavyPrimary
              ),
              modifier = Modifier.testTag("system_mock_toggle")
            )
          }

          if (systemMessage != null) {
            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = systemMessage,
                color = Color(0xFF38BDF8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { viewModel.pushMockLocationExplicitly() },
              modifier = Modifier.weight(1f)
            ) {
              Text("Push GPS Fix Now", fontSize = 11.sp)
            }
            OutlinedButton(
              onClick = { isAdbGuideOpen = !isAdbGuideOpen },
              modifier = Modifier.weight(1f)
            ) {
              Text(if (isAdbGuideOpen) "Hide ADB" else "ADB Commands", fontSize = 11.sp)
            }
          }

          AnimatedVisibility(visible = isAdbGuideOpen) {
            Surface(
              color = Color(0xFF090D16),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "# Send Manila Fix directly via ADB:",
                  color = Color(0xFF94A3B8),
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
                val cmd1 = "adb emu geo fix 120.9842 14.5995"
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(cmd1, color = Color(0xFF4ADE80), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
                  IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(cmd1)) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.ContentCopy, null, tint = Color.White, modifier = Modifier.size(13.dp))
                  }
                }
              }
            }
          }
        }
      }
    }

    // Live NMEA 0183 Sentence Terminal
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF020617))
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.Terminal, null, tint = ManilaGoldAccent, modifier = Modifier.size(16.dp))
              Text(
                text = "Live NMEA 0183 Stream (\$GPRMC, \$GPGGA)",
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              IconButton(
                onClick = { viewModel.clearNmeaLogs() },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(Icons.Default.Delete, "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
              }
            }
          }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF0B0F19))
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            nmeaLogs.take(7).forEach { log ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = log.timestampText,
                  color = Color(0xFF64748B),
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp
                )
                Text(
                  text = log.rawText,
                  color = if (log.sentenceType == "GPRMC") Color(0xFF38BDF8) else Color(0xFF4ADE80),
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.5.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CoordinateCard(title: String, value: String, target: String, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = title,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        text = target,
        fontSize = 8.5.sp,
        color = Color(0xFF10B981)
      )
    }
  }
}

@Composable
fun TelemetryMini(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(icon, null, tint = ManilaGoldAccent, modifier = Modifier.size(14.dp))
      Spacer(modifier = Modifier.height(2.dp))
      Text(label, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(value, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
  }
}
