package edu.vladprn.filestorage.ui.screen.settings

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.vladprn.filestorage.R
import edu.vladprn.filestorage.data.ResourceManager
import edu.vladprn.filestorage.data.SharedPrefs
import edu.vladprn.filestorage.domain.interactor.StorageInteractor
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sharedPrefs: SharedPrefs,
    private val storageInteractor: StorageInteractor,
    private val resourceManager: ResourceManager,
) : ViewModel(), SettingsListener {

    var state by mutableStateOf(SettingsViewState())
        private set
    var snackbarHostState = SnackbarHostState()
        private set

    private var onFileUnload: ((String) -> Unit) = {}

    init {
        state = state.copy(
            isCompressImages = sharedPrefs.isCompressImages
        )
    }

    fun setOnFileUnload(onFileUnload: (String) -> Unit) {
        this.onFileUnload = onFileUnload
    }

    fun saveBackup(uri: Uri?) = viewModelScope.launch {
        uri ?: return@launch

        val result = storageInteractor.extractToZip(
            uri = uri,
            onProgress = { processedFiles, totalFiles ->
                state = state.copy(
                    backupProgressDialog = SettingsViewState.BackupProgressDialog(
                        isVisible = true,
                        backupProcessedFiles = processedFiles,
                        backupTotalFiles = totalFiles
                    )
                )
            }
        )

        state = state.copy(
            backupProgressDialog = SettingsViewState.BackupProgressDialog(
                isVisible = false
            )
        )

        val messageResId = if (result) R.string.backup_success else R.string.backup_failed
        snackbarHostState.showSnackbar(
            message = resourceManager.getString(messageResId)
        )
    }

    override fun toggleImageCompression() {
        val newValue = !state.isCompressImages
        state = state.copy(isCompressImages = newValue)
        sharedPrefs.isCompressImages = newValue
    }

    override fun onSaveBackupClick() {
        onFileUnload(FILE_NAME)
    }

    companion object {
        private const val FILE_NAME = "backup.zip"
    }
}

data class SettingsViewState(
    val isCompressImages: Boolean = false,
    val backupProgressDialog: BackupProgressDialog = BackupProgressDialog(),
) {
    data class BackupProgressDialog(
        val isVisible: Boolean = false,
        val backupProcessedFiles: Int = 0,
        val backupTotalFiles: Int = 0,
    )
}