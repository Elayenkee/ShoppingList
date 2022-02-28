package fr.alemanflorian.shoppinglist.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.alemanflorian.shoppinglist.data.model.ListeResponse
import fr.alemanflorian.shoppinglist.data.model.ProductResponse

@Database(entities = [ProductResponse::class, ListeResponse::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun listeDao(): ListeDao
}