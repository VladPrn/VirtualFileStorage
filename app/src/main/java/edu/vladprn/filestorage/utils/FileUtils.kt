package edu.vladprn.filestorage.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileNotFoundException

class FileUtils(
    private val context: Context
) {

    fun getFileInfo(uri: Uri): FileInfo {
        val mimeType = context.contentResolver.getType(uri)
        var name: String? = null
        var size = 0L
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    size = it.getLong(sizeIndex)
                }
            }
        }
        return FileInfo(
            name = name,
            mimeType = mimeType,
            size = size
        )
    }

    fun saveFile(file: File, uri: Uri): Boolean = try {
        file.inputStream().use { inputStream ->
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                inputStream.copyTo(outputStream)
                true
            } ?: false
        }
    } catch (_: Throwable) {
        false
    }
}

data class FileInfo(
    val name: String?,
    val mimeType: String?,
    val size: Long,
)