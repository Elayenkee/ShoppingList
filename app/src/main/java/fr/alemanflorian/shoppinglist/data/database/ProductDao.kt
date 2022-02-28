package fr.alemanflorian.shoppinglist.data.database

import androidx.room.*
import fr.alemanflorian.shoppinglist.data.model.ProductResponse

@Dao
interface ProductDao {
    @Query("SELECT COUNT(id) FROM product")
    fun count(): Int

    @Query("SELECT * FROM product ORDER BY unique_name ASC")
    fun getAll(): List<ProductResponse>

    @Query("SELECT * FROM product WHERE id = :id LIMIT 1")
    fun find(id: Long): ProductResponse?

    @Insert
    fun insert(product: ProductResponse):Long

    @Update
    fun save(product: ProductResponse)

    @Delete
    fun delete(product: ProductResponse)
}