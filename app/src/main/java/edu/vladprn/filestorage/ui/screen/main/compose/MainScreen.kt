package edu.vladprn.filestorage.ui.screen.main.compose

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.vladprn.filestorage.R
import edu.vladprn.filestorage.ui.screen.main.MainListener
import edu.vladprn.filestorage.ui.screen.main.MainViewModel
import edu.vladprn.filestorage.ui.screen.main.MainViewState
import edu.vladprn.filestorage.ui.theme.FileStorageTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state = viewModel.state

    Content(
        state = state,
        listener = viewModel,
        snackbarHostState = viewModel.snackbarHostState
    )

    BackHandler(state.isBackHandlerEnabled) {
        viewModel.onNavigateBack()
    }

    PickFile(viewModel)
    UnloadFile(viewModel)
}

@Composable
private fun Content(
    state: MainViewState,
    listener: MainListener,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { listener.onAddFileClick() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SearchBar(
                    state = state,
                    listener = listener
                )

                FileList(
                    state = state,
                    listener = listener
                )
            }

            if (state.inProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            if (state.addFileDialog.isVisible) {
                FileNameDialog(
                    modifier = Modifier
                        .imePadding()
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp)
                        .width(500.dp),
                    defaultFileName = state.addFileDialog.defaultFileName,
                    onConfirm = { listener.onAddFileConfirm(it) },
                    onCancel = { listener.onAddFileCancel() }
                )
            }
        }
    }

    state.galleryBitmap?.let { bitmap ->
        BitmapSwipeWidget(
            modifier = Modifier.navigationBarsPadding(),
            bitmap = bitmap,
            onLeftSwipe = { listener.onGalleryLeftSwipe() },
            onRightSwipe = { listener.onGalleryRightSwipe() },
        )
    }
}

@Composable
private fun SearchBar(
    state: MainViewState,
    listener: MainListener
) {
    TextField(
        value = state.searchQuery,
        onValueChange = { listener.onSearchTextChanged(it) },
        label = {
            Text(
                text = stringResource(R.string.query_label)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun FileList(
    state: MainViewState,
    listener: MainListener
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.items) { file ->
            FileItem(
                file = file,
                onAction = { action ->
                    listener.onItemAction(
                        file = file,
                        fileItemAction = action
                    )
                },
            )
        }
    }
}

@Composable
private fun PickFile(viewModel: MainViewModel) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.openFileUri(uri)
        }
    )

    viewModel.setOnFilePicker {
        filePickerLauncher.launch("*/*")
    }
}

@Composable
private fun UnloadFile(viewModel: MainViewModel) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*"),
        onResult = { uri: Uri? ->
            viewModel.saveFileUri(uri)
        }
    )

    viewModel.setOnFileUnload { input ->
        filePickerLauncher.launch(input)
    }
}

@Preview
@Composable
private fun Preview() {
    FileStorageTheme {
        Content(
            state = MainViewState(),
            listener = MainListener.empty,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}