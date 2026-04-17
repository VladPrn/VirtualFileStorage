package edu.vladprn.filestorage.ui.screen.settings.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.vladprn.filestorage.R
import edu.vladprn.filestorage.domain.MimeType
import edu.vladprn.filestorage.ui.screen.settings.SettingsListener
import edu.vladprn.filestorage.ui.screen.settings.SettingsViewModel
import edu.vladprn.filestorage.ui.screen.settings.SettingsViewState
import edu.vladprn.filestorage.ui.theme.FileStorageTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state = viewModel.state

    Content(
        state = state,
        listener = viewModel,
        snackbarHostState = viewModel.snackbarHostState
    )

    UnloadFile(viewModel)
    ImportFile(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: SettingsViewState,
    listener: SettingsListener,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_title))
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { listener.toggleImageCompression() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.compress_images_setting),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Checkbox(
                        checked = state.isCompressImages,
                        onCheckedChange = { listener.toggleImageCompression() }
                    )
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { listener.onSaveBackupClick() }
                        .padding(16.dp),
                    text = stringResource(R.string.save_backup_setting),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { listener.onImportBackupClick() }
                        .padding(16.dp),
                    text = stringResource(R.string.import_backup_setting),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Dialogs(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
                    .width(500.dp),
                dialog = state.dialog
            )
        }
    }
}

@Composable
private fun Dialogs(
    modifier: Modifier,
    dialog: SettingsViewState.Dialog
) {
    when (dialog) {
        is SettingsViewState.Dialog.BackupProgressDialog -> BackupProgressDialog(
            modifier = modifier,
            processedFiles = dialog.backupProcessedFiles,
            totalFiles = dialog.backupTotalFiles
        )

        is SettingsViewState.Dialog.ImportProgressDialog -> ImportProgressDialog(
            modifier = modifier,
            processedFiles = dialog.importProcessedFiles,
            totalFiles = dialog.importTotalFiles
        )

        SettingsViewState.Dialog.NONE -> Unit
    }
}

@Composable
private fun UnloadFile(viewModel: SettingsViewModel) {
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(MimeType.ZIP),
        onResult = { uri: Uri? ->
            viewModel.saveBackup(uri)
        }
    )

    viewModel.setOnFileUnload { input ->
        backupLauncher.launch(input)
    }
}

@Composable
private fun ImportFile(viewModel: SettingsViewModel) {
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.importBackup(uri)
        }
    )

    viewModel.setOnFileImport {
        importLauncher.launch(MimeType.ZIP)
    }
}

@Preview
@Composable
private fun Preview() {
    FileStorageTheme {
        Content(
            state = SettingsViewState(),
            listener = SettingsListener.empty,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}