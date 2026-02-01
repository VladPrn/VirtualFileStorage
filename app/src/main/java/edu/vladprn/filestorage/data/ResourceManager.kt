package edu.vladprn.filestorage.data

import android.content.Context
import androidx.annotation.StringRes

class ResourceManager(
    private val context: Context
) {

    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}