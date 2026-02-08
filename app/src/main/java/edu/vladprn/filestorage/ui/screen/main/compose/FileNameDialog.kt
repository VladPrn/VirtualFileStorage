package edu.vladprn.filestorage.ui.screen.main.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.vladprn.filestorage.R

@Composable
fun FileNameDialog(
    modifier: Modifier,
    defaultFileName: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var fileName by remember { mutableStateOf(defaultFileName) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.fileNameTitle),
                style = MaterialTheme.typography.headlineSmall
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = fileName,
                onValueChange = { fileName = it },
                label = {
                    Text(
                        text = stringResource(R.string.fileNameLabel)
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onCancel) {
                    Text(
                        text = stringResource(R.string.fileNameCancelButton)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    enabled = fileName.isNotEmpty(),
                    onClick = { onConfirm(fileName) }
                ) {
                    Text(
                        text = stringResource(R.string.fileNameConfirmButton)
                    )
                }
            }
        }
    }
}