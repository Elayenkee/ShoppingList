package fr.alemanflorian.shoppinglist.data.database

import android.content.Context
import androidx.room.Room

fun createDatabase(appContext: Context): AppDatabase
{
    System.err.println("RoomDatabase :: createDatabase")
    val db = Room.databaseBuilder(appContext, AppDatabase::class.java, "kotlin-architecture")
        .fallbackToDestructiveMigration()
        .build()
    return db
}

fun createProductDao(database: AppDatabase) = database.productDao()

fun createListeDao(database: AppDatabase) = database.listeDao()