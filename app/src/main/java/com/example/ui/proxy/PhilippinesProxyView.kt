package com.example.ui.proxy

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IpGeoDetails
import com.example.model.PhilippinesProxyNode
import com.example.model.ProxyFetchResult
import com.example.model.ProxyProtocol
import com.example.model.ProxyRoutingMode
import com.example.ui.theme.ManilaEmerald
import com.example.ui.theme.ManilaGoldAccent
import com.example.ui.theme.ManilaNavyPrimary
import com.example.ui.theme.ManilaScarlet
import com.example.viewmodel.ManilaSimViewModel

@Composable
fun PhilippinesProxyScreen(
  viewModel: ManilaSimViewModel,
  activeNode: PhilippinesProxyNode,
  proxyNodes: List<PhilippinesProxyNode>,
  isProxyEnabled: Boolean,
  routingMode: ProxyRoutingMode,
  latestResult: ProxyFetchResult?,
  isFetching: Boolean,
  geoDetails: IpGeoDetails,
  modifier: Modifier = Modifier,
) {
  var urlInput by remember { mutableStateOf("https://httpbin.org/ip") }
  var isCustomDialogOpen by remember { mutableStateOf(false) }
  val clipboardManager = LocalClipboardManager.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Hero Card: Live Philippines Proxy & IP Status
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
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(ManilaNavyPrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.VpnLock,
                  contentDescription = null,
                  tint = ManilaGoldAccent,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "Philippines Proxy & Web Gateway",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${activeNode.name} • ${activeNode.city}, PH",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = isProxyEnabled,
              onCheckedChange = { viewModel.toggleProxy(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = ManilaGoldAccent,
                checkedTrackColor = ManilaNavyPrimary
              ),
              modifier = Modifier.testTag("proxy_toggle_switch")
            )
          }

          // Active IP & Geolocation Box
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF090D16),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "ROUTED EXIT IP ADDRESS",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF94A3B8)
                )
                Surface(
                  color = if (isProxyEnabled) Color(0xFF15803D) else Color(0xFF7F1D1D),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = if (isProxyEnabled) "PH PROXY ACTIVE" else "DIRECT CONNECT",
                    color = Color.White,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Text(
                text = geoDetails.ip,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = ManilaGoldAccent
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "📍 ${geoDetails.city}, ${geoDetails.region}",
                  fontSize = 10.sp,
                  color = Color.White
                )
                Text(
                  text = "🕒 ${geoDetails.timezone}",
                  fontSize = 10.sp,
                  color = Color(0xFF38BDF8)
                )
              }

              Text(
                text = "🏢 ISP: ${geoDetails.isp}",
                fontSize = 9.5.sp,
                color = Color(0xFFCBD5E1)
              )
            }
          }

          // Quick Specs Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ProxySpecBadge("PORT", activeNode.port.toString(), modifier = Modifier.weight(1f))
            ProxySpecBadge("PROTOCOL", activeNode.protocol.label, modifier = Modifier.weight(1f))
            ProxySpecBadge("ANONYMITY", activeNode.anonymLevel.split(" ").first(), modifier = Modifier.weight(1f))
            ProxySpecBadge("LATENCY", "${activeNode.latencyMs} ms", modifier = Modifier.weight(1f))
          }
        }
      }
    }

    // Live Web Fetch Proxy Test Console
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
            Icon(Icons.Default.Http, null, tint = ManilaNavyPrimary, modifier = Modifier.size(20.dp))
            Text(
              text = "Live Web Fetch via Philippines Proxy",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = "Execute live HTTP/HTTPS requests through the selected Philippines proxy node or Web Gateway relay.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // URL Input
          OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            label = { Text("Target URL to fetch via PH Proxy") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("target_url_input"),
            singleLine = true,
            trailingIcon = {
              IconButton(
                onClick = { viewModel.executeWebFetch(urlInput) },
                enabled = !isFetching
              ) {
                if (isFetching) {
                  CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                  Icon(Icons.Default.Send, "Fetch", tint = ManilaNavyPrimary)
                }
              }
            }
          )

          // Presets
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            AssistChip(
              onClick = {
                urlInput = "https://httpbin.org/ip"
                viewModel.executeWebFetch("https://httpbin.org/ip")
              },
              label = { Text("httpbin/ip", fontSize = 9.5.sp) }
            )
            AssistChip(
              onClick = {
                urlInput = "https://ipapi.co/json/"
                viewModel.executeWebFetch("https://ipapi.co/json/")
              },
              label = { Text("ipapi/json", fontSize = 9.5.sp) }
            )
            AssistChip(
              onClick = {
                urlInput = "https://httpbin.org/headers"
                viewModel.executeWebFetch("https://httpbin.org/headers")
              },
              label = { Text("Headers", fontSize = 9.5.sp) }
            )
          }

          // Fetch action button
          Button(
            onClick = { viewModel.executeWebFetch(urlInput) },
            enabled = !isFetching,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("fetch_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ManilaNavyPrimary)
          ) {
            if (isFetching) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Routing Request via ${activeNode.city} Proxy...")
            } else {
              Icon(Icons.Default.Public, null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Fetch via ${activeNode.name}")
            }
          }

          // Response Display
          if (latestResult != null) {
            Surface(
              color = Color(0xFF020617),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                    Surface(
                      color = if (latestResult.isSuccess) Color(0xFF15803D) else Color(0xFFB91C1C),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = "HTTP ${latestResult.statusCode}",
                        color = Color.White,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                    Text(
                      text = "${latestResult.durationMs} ms",
                      color = Color(0xFF94A3B8),
                      fontSize = 9.5.sp,
                      fontFamily = FontFamily.Monospace
                    )
                  }
                  IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(latestResult.bodyExcerpt)) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.ContentCopy, "Copy", tint = Color.White, modifier = Modifier.size(14.dp))
                  }
                }

                Text(
                  text = "Proxy Route: ${latestResult.proxyUsed}",
                  color = ManilaGoldAccent,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.SemiBold
                )

                Text(
                  text = "Exit IP: ${latestResult.resolvedIp} (${latestResult.city}, ${latestResult.country})",
                  color = Color(0xFF38BDF8),
                  fontSize = 9.5.sp,
                  fontFamily = FontFamily.Monospace
                )

                // Response Body Preview
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF090D16))
                    .padding(8.dp)
                ) {
                  Text(
                    text = latestResult.bodyExcerpt.ifEmpty { "Empty response body" },
                    color = Color(0xFF4ADE80),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
            }
          }
        }
      }
    }

    // Routing Strategy Selector
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
            text = "Proxy Routing Strategy",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          ProxyRoutingMode.values().forEach { mode ->
            val isSelected = routingMode == mode
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { viewModel.setRoutingMode(mode) }
                .border(
                  width = if (isSelected) 1.5.dp else 0.dp,
                  color = if (isSelected) ManilaGoldAccent else Color.Transparent,
                  shape = RoundedCornerShape(10.dp)
                ),
              color = if (isSelected) ManilaNavyPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = mode.title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.5.sp
                  )
                  if (isSelected) {
                    Icon(Icons.Default.Check, null, tint = ManilaGoldAccent, modifier = Modifier.size(16.dp))
                  }
                }
                Text(
                  text = mode.description,
                  fontSize = 9.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // Curated Philippines Proxy Servers
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
            Text(
              text = "Philippines Proxy Nodes (Asia/Manila)",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${proxyNodes.size} Nodes",
              fontSize = 10.sp,
              color = ManilaNavyPrimary,
              fontWeight = FontWeight.Bold
            )
          }

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            proxyNodes.forEach { node ->
              val isSelected = activeNode.id == node.id
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { viewModel.selectProxyNode(node) }
                  .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) ManilaGoldAccent else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                  )
                  .testTag("proxy_node_${node.id}"),
                color = if (isSelected) ManilaNavyPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Text(node.name, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                      Surface(
                        color = ManilaNavyPrimary,
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = node.protocol.label,
                          color = Color.White,
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                      }
                    }
                    Text(
                      text = "${node.host}:${node.port} • ${node.city} (${node.isp})",
                      fontSize = 9.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = "${node.latencyMs}ms",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (node.latencyMs < 45) Color(0xFF15803D) else Color(0xFFD97706)
                    )
                    if (isSelected) {
                      Surface(
                        color = Color(0xFF15803D),
                        shape = RoundedCornerShape(6.dp)
                      ) {
                        Text(
                          text = "ACTIVE",
                          color = Color.White,
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                      }
                    } else {
                      OutlinedButton(
                        onClick = { viewModel.selectProxyNode(node) },
                        modifier = Modifier.height(26.dp)
                      ) {
                        Text("Connect", fontSize = 9.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProxySpecBadge(label: String, value: String, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, fontSize = 7.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(value, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
    }
  }
}
