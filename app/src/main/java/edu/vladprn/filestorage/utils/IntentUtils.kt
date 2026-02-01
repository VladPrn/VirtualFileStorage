package edu.vladprn.filestorage.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class IntentUtils(
    private val context: Context
) {

    fun openFile(
        file: File,
        mimeType: String,
    ) {
        val contentUri = FileProvider.getUriForFile(
            context,
            FILE_PROVIDER,
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    companion object {
        private const val FILE_PROVIDER = "edu.vladprn.filestorage.fileprovider"
    }
}