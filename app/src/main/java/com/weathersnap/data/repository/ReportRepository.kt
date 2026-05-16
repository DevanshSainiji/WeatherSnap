package com.weathersnap.data.repository

import com.weathersnap.data.local.ReportDao
import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.domain.model.Report
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {

    suspend fun saveReport(report: Report) {
        reportDao.insertReport(report.toEntity())
    }

    fun getAllReports(): Flow<List<Report>> {
        return reportDao.getAllReports().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getReportCount(): Flow<Int> {
        return reportDao.getReportCount()
    }

    private fun Report.toEntity(): ReportEntity {
        return ReportEntity(
            id = if (id == 0) 0 else id,
            cityName = cityName,
            temperature = temperature,
            condition = condition,
            humidity = humidity,
            windSpeed = windSpeed,
            pressure = pressure,
            imagePath = imagePath,
            originalImageSize = originalImageSize,
            compressedImageSize = compressedImageSize,
            notes = notes,
            timestamp = timestamp
        )
    }

    private fun ReportEntity.toDomain(): Report {
        return Report(
            id = id,
            cityName = cityName,
            temperature = temperature,
            condition = condition,
            humidity = humidity,
            windSpeed = windSpeed,
            pressure = pressure,
            imagePath = imagePath,
            originalImageSize = originalImageSize,
            compressedImageSize = compressedImageSize,
            notes = notes,
            timestamp = timestamp
        )
    }
}
