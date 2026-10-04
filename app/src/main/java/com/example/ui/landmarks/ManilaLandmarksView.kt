package com.example.ui.landmarks

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ManilaLandmark
import com.example.model.ManilaLocationState
import com.example.ui.theme.ManilaGoldAccent
import com.example.ui.theme.ManilaNavyPrimary
import com.example.ui.theme.ManilaScarlet
import com.example.viewmodel.ManilaSimViewModel
import java.util.Locale

@Composable
fun ManilaLandmarksScreen(
  viewModel: ManilaSimViewModel,
  locationState: ManilaLocationState,
  modifier: Modifier = Modifier,
) {
  val landmarks = viewModel.locationEngine.landmarks

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Manila Landmark Teleporter",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Surface(
              color = ManilaNavyPrimary,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "${landmarks.size} SITES",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Select any historic or modern Manila point of interest to instantly jump the virtual device GPS fix.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    items(landmarks) { landmark ->
      val isCurrent = Math.abs(landmark.latitude - locationState.latitude) < 0.0005 &&
          Math.abs(landmark.longitude - locationState.longitude) < 0.0005

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.jumpToLandmark(landmark) }
          .border(
            width = if (isCurrent) 2.dp else 0.dp,
            color = if (isCurrent) ManilaGoldAccent else Color.Transparent,
            shape = RoundedCornerShape(14.dp)
          )
          .testTag("landmark_card_${landmark.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isCurrent) ManilaNavyPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        )
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
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(if (isCurrent) ManilaNavyPrimary else MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isCurrent) Icons.Default.Check else Icons.Default.Place,
                  contentDescription = null,
                  tint = if (isCurrent) ManilaGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
              }
              Column {
                Text(
                  text = landmark.name,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = landmark.category,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Surface(
              color = if (landmark.id == "city_hall") ManilaScarlet else MaterialTheme.colorScheme.surface,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = landmark.tag,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (landmark.id == "city_hall") Color.White else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = landmark.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = String.format(Locale.US, "%.4f° N, %.4f° E", landmark.latitude, landmark.longitude),
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              color = Color(0xFF38BDF8),
              fontWeight = FontWeight.SemiBold
            )

            if (isCurrent) {
              Surface(
                color = Color(0xFF15803D),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "ACTIVE LOCATION",
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            } else {
              OutlinedButton(
                onClick = { viewModel.jumpToLandmark(landmark) },
                modifier = Modifier.height(28.dp)
              ) {
                Text("Jump GPS Here", fontSize = 10.sp)
              }
            }
          }
        }
      }
    }
  }
}
