package com.weathersnap.ui.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.weathersnap.ui.theme.AccentPrimary
import com.weathersnap.ui.theme.AccentTeal
import com.weathersnap.ui.theme.BannerGradientEnd
import com.weathersnap.ui.theme.BannerGradientStart
import com.weathersnap.ui.theme.CardDark
import com.weathersnap.ui.theme.HeaderGradientEnd
import com.weathersnap.ui.theme.HeaderGradientStart
import com.weathersnap.ui.theme.OutlineColor
import com.weathersnap.ui.theme.SurfaceDark
import com.weathersnap.ui.theme.TextOnAccent
import com.weathersnap.ui.theme.TextPrimary
import com.weathersnap.ui.theme.TextSecondary
import com.weathersnap.ui.weather.StatCard
import java.io.File

import com.weathersnap.ui.theme.BackgroundGradientEnd
import com.weathersnap.ui.theme.BackgroundGradientMiddle
import com.weathersnap.ui.theme.BackgroundGradientStart

@Composable
fun CreateReportScreen(
    viewModel: ReportViewModel,
    cityName: String,
    temperature: Double,
    condition: String,
    humidity: Int,
    windSpeed: Double,
    pressure: Double,
    onNavigateToCamera: () -> Unit,
    onNavigateToSavedReports: () -> Unit,
    onBack: () -> Unit
) {
    val draftImagePath by viewModel.draftImagePath.collectAsStateWithLifecycle()
    val draftOriginalSize by viewModel.draftOriginalSize.collectAsStateWithLifecycle()
    val draftCompressedSize by viewModel.draftCompressedSize.collectAsStateWithLifecycle()
    val draftNotes by viewModel.draftNotes.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    val hasImage = !draftImagePath.isNullOrEmpty()

    // Navigate to saved reports after successful save
    LaunchedEffect(saveState) {
        if (saveState is SaveUiState.Saved) {
            viewModel.resetSaveState()
            onNavigateToSavedReports()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientMiddle, BackgroundGradientEnd)
                )
            )
            .verticalScroll(rememberScrollState())
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
                    text = "Create Report",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal
                )
                Text(
                    text = "Capture, compress, annotate",
                    fontSize = 13.sp,
                    color = AccentTeal.copy(alpha = 0.8f)
                )
            }
            Button(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather Details Card (frozen snapshot)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cityName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = condition,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${temperature.toInt()}°C",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "Humidity",
                        value = "${humidity}%",
                        valueColor = androidx.compose.ui.graphics.Color(0xFF4DB6AC),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Wind",
                        value = "${windSpeed} m/s",
                        valueColor = androidx.compose.ui.graphics.Color(0xFF64B5F6),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Pressure",
                        value = "${pressure.toInt()}",
                        valueColor = androidx.compose.ui.graphics.Color(0xFFFFB300),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Photo Preview Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            Column {
                // Image preview area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (hasImage) CardDark
                            else AccentPrimary // Fallback color since brush cannot be easily swapped conditionally in this modifier setup without extra wrapper
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasImage) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            AsyncImage(
                                model = File(draftImagePath!!),
                                contentDescription = "Captured photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Text(
                            text = "Photo preview",
                            fontSize = 16.sp,
                            color = TextOnAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Capture / Retake button
                Button(
                    onClick = onNavigateToCamera,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary,
                        contentColor = TextOnAccent
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = if (hasImage) "Retake Photo" else "Capture Photo",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Image size cards (visible only after capture)
                AnimatedVisibility(visible = hasImage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
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
                                    text = "${draftOriginalSize / 1024} KB",
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
                                    text = "${draftCompressedSize / 1024} KB",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Field Notes Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Field Notes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = draftNotes,
                    onValueChange = { viewModel.onNotesChanged(it) },
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = OutlineColor,
                        focusedLabelColor = AccentPrimary,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = AccentPrimary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Report Button
        Button(
            onClick = {
                viewModel.saveReport(
                    cityName = cityName,
                    temperature = temperature,
                    condition = condition,
                    humidity = humidity,
                    windSpeed = windSpeed,
                    pressure = pressure
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasImage && saveState !is SaveUiState.Saving,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPrimary,
                contentColor = TextOnAccent,
                disabledContainerColor = AccentPrimary.copy(alpha = 0.4f),
                disabledContentColor = TextOnAccent.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = if (saveState is SaveUiState.Saving) "Saving..." else "Save Report",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
