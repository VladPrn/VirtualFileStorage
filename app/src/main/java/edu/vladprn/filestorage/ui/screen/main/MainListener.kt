package edu.vladprn.filestorage.ui.screen.main

import edu.vladprn.filestorage.ui.screen.main.compose.FileItemAction

interface MainListener {
    fun onSearchTextChanged(query: String)
    fun onAddFileClick()
    fun onAddFileConfirm(fileName: String)
    fun onAddFileCancel()
    fun onGalleryLeftSwipe()
    fun onGalleryRightSwipe()
    fun onItemAction(file: FileUIModel, fileItemAction: FileItemAction)
    fun onNavigateBack()
    fun onSettingsClick()

    companion object {
        val empty = object : MainListener {
            override fun onSearchTextChanged(query: String) = Unit
            override fun onAddFileClick() = Unit
            override fun onAddFileConfirm(fileName: String) = Unit
            override fun onAddFileCancel() = Unit
            override fun onGalleryLeftSwipe() = Unit
            override fun onGalleryRightSwipe() = Unit
            override fun onNavigateBack() = Unit
            override fun onSettingsClick() = Unit

            override fun onItemAction(
                file: FileUIModel,
                fileItemAction: FileItemAction
            ) = Unit

        }
    }
}