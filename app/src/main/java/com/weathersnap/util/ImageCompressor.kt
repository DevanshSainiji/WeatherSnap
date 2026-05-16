package com.weathersnap.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class CompressedImageResult(
    val compressedPath: String,
    val originalSize: Long,
    val compressedSize: Long
)

@Singleton
class ImageCompressor @Inject constructor() {

    fun compressImage(context: Context, originalPath: String): CompressedImageResult {
        val originalFile = File(originalPath)
        val originalSize = originalFile.length()

        val bitmap = BitmapFactory.decodeFile(originalPath)
        val compressedFile = File(
            context.filesDir,
            "report_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(compressedFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        }
        bitmap.recycle()

        val compressedSize = compressedFile.length()

        return CompressedImageResult(
            compressedPath = compressedFile.absolutePath,
            originalSize = originalSize,
            compressedSize = compressedSize
        )
    }

    fun deleteTempFile(path: String) {
        try {
            File(path).delete()
        } catch (_: Exception) { }
    }
}
