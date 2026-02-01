package edu.vladprn.filestorage.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.vladprn.filestorage.ui.screen.main.compose.MainScreen
import edu.vladprn.filestorage.ui.theme.FileStorageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileStorageTheme {
                MainScreen()
            }
        }
    }
}