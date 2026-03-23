package edu.vladprn.filestorage.ui.screen.settings

interface SettingsListener {
    fun toggleImageCompression()
    fun onSaveBackupClick()

    companion object {
        val empty = object : SettingsListener {
            override fun toggleImageCompression() = Unit
            override fun onSaveBackupClick() = Unit
        }
    }
}