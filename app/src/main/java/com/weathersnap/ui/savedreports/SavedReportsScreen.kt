package com.weathersnap.ui.savedreports

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.weathersnap.domain.model.Report
import com.weathersnap.ui.report.ReportViewModel
import com.weathersnap.ui.theme.AccentPrimary
import com.weathersnap.ui.theme.AccentTeal
import com.weathersnap.ui.theme.BannerGradientEnd
import com.weathersnap.ui.theme.BannerGradientStart
import com.weathersnap.ui.theme.CardDark
import com.weathersnap.ui.theme.HeaderGradientEnd
import com.weathersnap.ui.theme.HeaderGradientStart
import com.weathersnap.ui.theme.SurfaceDark
import com.weathersnap.ui.theme.TextOnAccent
import com.weathersnap.ui.theme.TextPrimary
import com.weathersnap.ui.theme.TextSecondary
import com.weathersnap.ui.theme.AccentTeal
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.weathersnap.ui.theme.BackgroundGradientEnd
import com.weathersnap.ui.theme.BackgroundGradientStart

@Composable
fun SavedReportsScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val reportCount by viewModel.reportCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            )
            .padding(16.dp)
    ) {
        // Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(HeaderGradientStart, HeaderGradientEnd)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Saved Reports",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal
                )
                Text(
                    text = "$reportCount ${if (reportCount == 1) "report" else "reports"} stored locally",
                    fontSize = 13.sp,
                    color = AccentTeal.copy(alpha = 0.8f)
                )
            }
            Button(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (reports.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .padding(16.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(BannerGradientStart, BannerGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reports yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Create and save a weather report to see image, notes, and weather details here.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reports) { report ->
                    ReportCard(report = report)
                }
            }
        }
    }
}

@Composable
private fun ReportCard(report: Report) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
    ) {
        Column {
            // Captured image
            AsyncImage(
                model = File(report.imagePath),
                contentDescription = "Report photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // City name and temperature
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = report.cityName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = report.condition,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = formatTimestamp(report.timestamp),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AccentPrimary)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${report.temperature.toInt()}°C",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextOnAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Image size cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDark)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Original", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${report.originalImageSize / 1024} KB",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentPrimary
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDark)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Compressed", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${report.compressedImageSize / 1024} KB",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentPrimary
                            )
                        }
                    }
                }

                // Notes
                if (report.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = report.notes,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
