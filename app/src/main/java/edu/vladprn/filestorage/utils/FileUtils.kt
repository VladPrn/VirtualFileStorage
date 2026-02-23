package edu.vladprn.filestorage.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File

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

    fun openFile(
        file: File,
        mimeType: String,
    ) {
        val contentUri = getUriFromFile(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun getUriFromFile(file: File): Uri = FileProvider.getUriForFile(
        context,
        FILE_PROVIDER,
        file
    )

    companion object {
        private const val FILE_PROVIDER = "edu.vladprn.filestorage.fileprovider"
    }
}

data class FileInfo(
    val name: String?,
    val mimeType: String?,
    val size: Long,
)