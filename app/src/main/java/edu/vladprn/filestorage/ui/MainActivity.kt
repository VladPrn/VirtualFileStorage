package edu.vladprn.filestorage.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.vladprn.filestorage.ui.screen.AppScreen
import edu.vladprn.filestorage.ui.screen.main.compose.MainScreen
import edu.vladprn.filestorage.ui.screen.settings.compose.SettingsScreen
import edu.vladprn.filestorage.ui.theme.FileStorageTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val appNavigator by inject<AppNavigator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileStorageTheme {
                val navController = rememberNavController()

                appNavigator.setNavController(navController)

                NavHost(
                    navController = navController,
                    startDestination = AppScreen.MAIN
                ) {
                    composable(AppScreen.MAIN) {
                        MainScreen()
                    }
                    composable(AppScreen.SETTINGS) {
                        SettingsScreen()
                    }
                }
            }
        }
    }
}