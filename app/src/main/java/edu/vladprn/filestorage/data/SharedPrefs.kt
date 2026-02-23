package edu.vladprn.filestorage.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefs(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_HAME,
        Context.MODE_PRIVATE
    )

    var isCompressImages: Boolean
        get() = prefs.getBoolean(IS_COMPRESS_IMAGES, false)
        set(value) {
            prefs.edit {
                putBoolean(IS_COMPRESS_IMAGES, value)
            }
        }

    companion object {
        private const val SHARED_PREFS_HAME = "file_storage_prefs"
        private const val IS_COMPRESS_IMAGES = "is_compress_images"
    }
}