package edu.vladprn.filestorage.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.vladprn.filestorage.data.database.dao.AddressDao
import edu.vladprn.filestorage.data.database.dao.FileDao
import edu.vladprn.filestorage.data.database.entity.AddressEntity
import edu.vladprn.filestorage.data.database.entity.FileEntity

@Database(
    entities = [FileEntity::class, AddressEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao
    abstract fun addressDao(): AddressDao

    companion object {
        fun getDatabase(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "storage_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}