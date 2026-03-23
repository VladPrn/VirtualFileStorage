package edu.vladprn.filestorage.ui.screen.settings.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edu.vladprn.filestorage.R

@Composable
fun BackupProgressDialog(
    modifier: Modifier,
    processedFiles: Int,
    totalFiles: Int,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.backup_progress_title),
                style = MaterialTheme.typography.headlineSmall
            )

            LinearProgressIndicator(
                progress = { if (totalFiles > 0) processedFiles.toFloat() / totalFiles else 0f },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(
                    id = R.string.backup_progress_desc,
                    processedFiles,
                    totalFiles
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}