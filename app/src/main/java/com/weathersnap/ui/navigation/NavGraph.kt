package com.weathersnap.ui.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.camera.CameraScreen
import com.weathersnap.ui.report.CreateReportScreen
import com.weathersnap.ui.report.ReportViewModel
import com.weathersnap.ui.savedreports.SavedReportsScreen
import com.weathersnap.ui.weather.WeatherScreen
import com.weathersnap.ui.weather.WeatherViewModel

object Routes {
    const val WEATHER = "weather"
    const val CREATE_REPORT = "createReport/{cityName}/{temperature}/{condition}/{humidity}/{windSpeed}/{pressure}"
    const val CAMERA = "camera"
    const val SAVED_REPORTS = "savedReports"

    fun createReportRoute(weather: Weather): String {
        return "createReport/${Uri.encode(weather.cityName)}/${weather.temperature}/${Uri.encode(weather.condition)}/${weather.humidity}/${weather.windSpeed}/${weather.pressure}"
    }
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        // Weather Screen
        composable(Routes.WEATHER) {
            val weatherViewModel: WeatherViewModel = hiltViewModel()
            WeatherScreen(
                viewModel = weatherViewModel,
                onNavigateToReports = {
                    navController.navigate(Routes.SAVED_REPORTS)
                },
                onNavigateToCreateReport = { weather ->
                    navController.navigate(Routes.createReportRoute(weather))
                }
            )
        }

        // Create Report Screen
        composable(
            route = Routes.CREATE_REPORT,
            arguments = listOf(
                navArgument("cityName") { type = NavType.StringType },
                navArgument("temperature") { type = NavType.FloatType },
                navArgument("condition") { type = NavType.StringType },
                navArgument("humidity") { type = NavType.IntType },
                navArgument("windSpeed") { type = NavType.FloatType },
                navArgument("pressure") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val reportViewModel: ReportViewModel = hiltViewModel()

            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val temperature = backStackEntry.arguments?.getFloat("temperature")?.toDouble() ?: 0.0
            val condition = backStackEntry.arguments?.getString("condition") ?: ""
            val humidity = backStackEntry.arguments?.getInt("humidity") ?: 0
            val windSpeed = backStackEntry.arguments?.getFloat("windSpeed")?.toDouble() ?: 0.0
            val pressure = backStackEntry.arguments?.getFloat("pressure")?.toDouble() ?: 0.0

            // Listen for camera result
            val savedStateHandle = backStackEntry.savedStateHandle
            val imagePath = savedStateHandle.get<String>("capturedImagePath")
            if (imagePath != null) {
                val context = navController.context
                reportViewModel.processImage(context, imagePath)
                savedStateHandle.remove<String>("capturedImagePath")
            }

            CreateReportScreen(
                viewModel = reportViewModel,
                cityName = cityName,
                temperature = temperature,
                condition = condition,
                humidity = humidity,
                windSpeed = windSpeed,
                pressure = pressure,
                onNavigateToCamera = {
                    navController.navigate(Routes.CAMERA)
                },
                onNavigateToSavedReports = {
                    navController.navigate(Routes.SAVED_REPORTS) {
                        popUpTo(Routes.WEATHER) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Camera Screen
        composable(Routes.CAMERA) {
            CameraScreen(
                onImageCaptured = { path ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("capturedImagePath", path)
                    navController.popBackStack()
                },
                onClose = { navController.popBackStack() }
            )
        }

        // Saved Reports Screen
        composable(Routes.SAVED_REPORTS) {
            val reportViewModel: ReportViewModel = hiltViewModel()
            SavedReportsScreen(
                viewModel = reportViewModel,
                onBack = {
                    navController.navigate(Routes.WEATHER) {
                        popUpTo(Routes.WEATHER) { inclusive = true }
                    }
                }
            )
        }
    }
}
