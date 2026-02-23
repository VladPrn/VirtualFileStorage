package edu.vladprn.filestorage.ui.screen.settings

interface SettingsListener {
    fun toggleImageCompression()

    companion object {
        val empty = object : SettingsListener {
            override fun toggleImageCompression() = Unit
        }
    }
}