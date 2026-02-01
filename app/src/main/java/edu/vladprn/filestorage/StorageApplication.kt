package edu.vladprn.filestorage

import android.app.Application
import edu.vladprn.filestorage.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StorageApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@StorageApplication)

            modules(MainModule)
        }
    }
}