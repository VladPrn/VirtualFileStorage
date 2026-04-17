package edu.vladprn.filestorage.ui.screen.settings.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.vladprn.filestorage.R

@Composable
fun ImportProgressDialog(
    modifier: Modifier,
    processedFiles: Int,
    totalFiles: Int,
) {
    ProgressDialog(
        modifier = modifier,
        titleResId = R.string.import_progress_title,
        descResId = R.string.import_progress_desc,
        processedFiles = processedFiles,
        totalFiles = totalFiles
    )
}
