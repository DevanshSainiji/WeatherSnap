package com.weathersnap.ui.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.theme.AccentPrimary
import com.weathersnap.ui.theme.AccentTeal
import com.weathersnap.ui.theme.BackgroundGradientEnd
import com.weathersnap.ui.theme.BackgroundGradientMiddle
import com.weathersnap.ui.theme.BackgroundGradientStart
import com.weathersnap.ui.theme.BannerGradientEnd
import com.weathersnap.ui.theme.BannerGradientStart
import com.weathersnap.ui.theme.CardDark
import com.weathersnap.ui.theme.HeaderGradientEnd
import com.weathersnap.ui.theme.HeaderGradientStart
import com.weathersnap.ui.theme.OutlineColor
import com.weathersnap.ui.theme.SurfaceDark
import com.weathersnap.ui.theme.SurfaceVariant
import com.weathersnap.ui.theme.TextOnAccent
import com.weathersnap.ui.theme.TextPrimary
import com.weathersnap.ui.theme.TextSecondary

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onNavigateToReports: () -> Unit,
    onNavigateToCreateReport: (Weather) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val suggestionState by viewModel.suggestionState.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()

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
                    text = "WeatherSnap",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal
                )
                Text(
                    text = "Live weather reports with camera evidence",
                    fontSize = 13.sp,
                    color = AccentTeal.copy(alpha = 0.8f)
                )
            }
            Button(
                onClick = onNavigateToReports,
                modifier = Modifier.align(Alignment.CenterEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reports", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = OutlineColor,
                            focusedLabelColor = AccentPrimary,
                            unfocusedLabelColor = TextSecondary,
                            cursorColor = AccentPrimary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { viewModel.fetchWeather() },
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPrimary,
                            contentColor = AccentTeal
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (weatherState is WeatherUiState.Loading) {
                            Text("...", fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("Search", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter more than 2 letters to start city suggestions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Loading cities indicator
        AnimatedVisibility(
            visible = suggestionState is SuggestionUiState.Loading,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = AccentPrimary,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Finding cities...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // City Suggestions
        AnimatedVisibility(
            visible = suggestionState is SuggestionUiState.Success,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val cities = (suggestionState as? SuggestionUiState.Success)?.cities ?: emptyList()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cities.forEach { city ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, OutlineColor, RoundedCornerShape(24.dp))
                            .clickable { viewModel.onCitySelected(city) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = city.displayName,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Suggestion error state
        AnimatedVisibility(
            visible = suggestionState is SuggestionUiState.Empty,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .padding(16.dp)
            ) {
                Text("No cities found.", color = TextSecondary, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weather Display Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            when (val state = weatherState) {
                is WeatherUiState.Idle -> {
                    // Empty state
                    Column {
                        // Gradient banner
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
                                text = "Search. Capture. Save.",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No weather loaded",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter more than 2 letters, choose a city, then search.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                is WeatherUiState.Loading -> {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = AccentPrimary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Loading weather...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Fetching coordinates and current conditions",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Shimmer placeholders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardDark)
                                )
                            }
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    WeatherContent(
                        weather = state.weather,
                        onCreateReport = { onNavigateToCreateReport(state.weather) }
                    )
                }

                is WeatherUiState.Error -> {
                    Column {
                        Text(
                            text = "Error loading weather",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFCF6679)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(
    weather: Weather,
    onCreateReport: () -> Unit
) {
    Column {
        // City name and temperature
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = weather.cityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                Text(
                    text = weather.condition,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
            // Temperature pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3F4C1A)) // Dark olive background for temperature
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${weather.temperature.toInt()}°C",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = AccentPrimary // Bright green text
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "Humidity",
                value = "${weather.humidity}%",
                valueColor = Color(0xFF4DB6AC), // Teal
                backgroundColor = Color(0xFF2E3A39),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Wind",
                value = String.format(java.util.Locale.US, "%.2f m/s", weather.windSpeed),
                valueColor = Color(0xFF64B5F6), // Blue
                backgroundColor = Color(0xFF2D3944),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Pressure",
                value = "${weather.pressure.toInt()}",
                valueColor = Color(0xFFFFB300), // Orange
                backgroundColor = Color(0xFF3E3628),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Report readiness
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceVariant)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Report readiness",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Text(
                text = "Camera and Room DB enabled",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Report button
        Button(
            onClick = onCreateReport,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPrimary,
                contentColor = TextOnAccent
            )
        ) {
            Text(
                text = "Create Report",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    valueColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}
