package edu.vladprn.filestorage.ui

import androidx.navigation.NavController

class AppNavigator {

    private var navController: NavController? = null

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun navController(navController: (NavController) -> Unit) {
        this.navController?.let { navController(it) }
    }
}