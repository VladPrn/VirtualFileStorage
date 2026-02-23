package edu.vladprn.filestorage.ui.screen.settings.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.vladprn.filestorage.R
import edu.vladprn.filestorage.ui.screen.settings.SettingsListener
import edu.vladprn.filestorage.ui.screen.settings.SettingsViewModel
import edu.vladprn.filestorage.ui.screen.settings.SettingsViewState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state = viewModel.state

    Content(
        state = state,
        listener = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: SettingsViewState,
    listener: SettingsListener
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_title))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { listener.toggleImageCompression() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.compress_images_setting),
                    style = MaterialTheme.typography.bodyLarge
                )
                Checkbox(
                    checked = state.isCompressImages,
                    onCheckedChange = { listener.toggleImageCompression() }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Content(
        state = SettingsViewState(),
        listener = SettingsListener.empty
    )
}