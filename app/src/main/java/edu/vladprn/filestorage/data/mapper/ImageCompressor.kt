package edu.vladprn.filestorage.data.mapper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import edu.vladprn.filestorage.data.SharedPrefs
import edu.vladprn.filestorage.domain.MimeType
import edu.vladprn.filestorage.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageCompressor(
    private val context: Context,
    private val fileUtils: FileUtils,
    private val sharedPrefs: SharedPrefs,
) {

    suspend fun processImage(uri: Uri): Uri {
        val fileInfo = fileUtils.getFileInfo(uri)
        return if (sharedPrefs.isCompressImages && fileInfo.mimeType == MimeType.PNG) {
            val newUri = compressImage(
                uri = uri,
                fileName = fileInfo.name?.changeExtensionToJpg() ?: DEFAULT_NAME
            )
            newUri ?: uri
        } else {
            uri
        }
    }

    private suspend fun compressImage(
        uri: Uri,
        fileName: String
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = inputStream?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return@withContext null

            val folder = File(context.filesDir, SHARE)
            val file = File(folder, fileName)
            folder.mkdir()

            file.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
            }
            fileUtils.getUriFromFile(file)
        } catch (_: Throwable) {
            null
        }
    }

    private fun String?.changeExtensionToJpg(): String? {
        if (isNullOrEmpty()) return null

        val lastDotIndex = lastIndexOf('.')
        val name = if (lastDotIndex > 0) {
            substring(0, lastDotIndex)
        } else {
            this
        }
        return "$name.jpg"
    }

    companion object {
        private const val DEFAULT_NAME = "file.jpg"
        private const val SHARE = "share"
        private const val QUALITY = 100
    }
}