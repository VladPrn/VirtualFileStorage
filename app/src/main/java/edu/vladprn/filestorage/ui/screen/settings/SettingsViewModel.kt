package edu.vladprn.filestorage.ui.screen.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import edu.vladprn.filestorage.data.SharedPrefs

class SettingsViewModel(
    private val sharedPrefs: SharedPrefs
) : ViewModel(), SettingsListener {

    var state by mutableStateOf(SettingsViewState())

    init {
        state = state.copy(
            isCompressImages = sharedPrefs.isCompressImages
        )
    }

    override fun toggleImageCompression() {
        val newValue = !state.isCompressImages
        state = state.copy(isCompressImages = newValue)
        sharedPrefs.isCompressImages = newValue
    }
}

data class SettingsViewState(
    val isCompressImages: Boolean = false
)