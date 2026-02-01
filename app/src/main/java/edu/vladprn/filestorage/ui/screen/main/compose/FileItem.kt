package edu.vladprn.filestorage.ui.screen.main.compose

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import edu.vladprn.filestorage.R
import edu.vladprn.filestorage.ui.screen.main.FileUIModel

@Composable
fun FileItem(
    file: FileUIModel,
    onAction: (FileItemAction) -> Unit,
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }

    Box {
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
            offset = DpOffset(
                x = pressOffset.x.dp,
                y = 0.dp
            )
        ) {
            if (file.isImage) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.file_item_open)
                        )
                    },
                    onClick = {
                        onAction(FileItemAction.OPEN)
                        showContextMenu = false
                    }
                )
            }
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.file_item_open_external)
                    )
                },
                onClick = {
                    onAction(FileItemAction.OPEN_EXTERNAL)
                    showContextMenu = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.file_item_unload)
                    )
                },
                onClick = {
                    onAction(FileItemAction.UNLOAD)
                    showContextMenu = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.file_item_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    onAction(FileItemAction.DELETE)
                    showContextMenu = false
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onAction(FileItemAction.CLICK)
                        },
                        onLongPress = { offset ->
                            pressOffset = offset
                            showContextMenu = true
                        }
                    )
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = file.size,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

enum class FileItemAction {
    CLICK,
    OPEN,
    OPEN_EXTERNAL,
    UNLOAD,
    DELETE
}