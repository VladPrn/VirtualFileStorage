package edu.vladprn.filestorage.di

import edu.vladprn.filestorage.data.FileSystemModel
import edu.vladprn.filestorage.data.ResourceManager
import edu.vladprn.filestorage.data.SharedPrefs
import edu.vladprn.filestorage.data.database.AppDatabase
import edu.vladprn.filestorage.data.mapper.AddressEntityMapper
import edu.vladprn.filestorage.data.mapper.ImageCompressor
import edu.vladprn.filestorage.data.mapper.FileEntityMapper
import edu.vladprn.filestorage.domain.interactor.StorageInteractor
import edu.vladprn.filestorage.domain.interactor.StorageAllocator
import edu.vladprn.filestorage.data.repository.FileRepository
import edu.vladprn.filestorage.ui.AppNavigator
import edu.vladprn.filestorage.ui.screen.main.MainViewModel
import edu.vladprn.filestorage.ui.screen.settings.SettingsViewModel
import edu.vladprn.filestorage.utils.FileUtils
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val MainModule = module {

    single<AppDatabase> {
        AppDatabase.getDatabase(context = get())
    }
    singleOf(::SharedPrefs)
    singleOf(::AppNavigator)

    factoryOf(::FileRepository)
    factoryOf(::StorageAllocator)
    factoryOf(::FileEntityMapper)
    factoryOf(::AddressEntityMapper)
    factoryOf(::ImageCompressor)
    factoryOf(::StorageInteractor)
    factoryOf(::FileUtils)
    factoryOf(::FileSystemModel)
    factoryOf(::ResourceManager)

    viewModelOf(::MainViewModel)
    viewModelOf(::SettingsViewModel)
}