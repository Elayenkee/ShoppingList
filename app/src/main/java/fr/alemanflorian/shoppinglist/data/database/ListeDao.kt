package fr.alemanflorian.shoppinglist.data.database

import androidx.room.*
import fr.alemanflorian.shoppinglist.common.error.LastListeException
import fr.alemanflorian.shoppinglist.data.model.ListeResponse
import fr.alemanflorian.shoppinglist.data.model.ProductResponse

@Dao
interface ListeDao {
    @Query("SELECT * FROM liste")
    fun getAll(): List<ListeResponse>

    @Query("SELECT * FROM liste WHERE id = :id LIMIT 1")
    fun find(id: Long): ListeResponse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(liste: ListeResponse):Long

    @Delete
    fun forceDelete(liste: ListeResponse)

    fun delete(liste: ListeResponse)
    {
        if(count() > 1)
            forceDelete(liste)
        else
            throw LastListeException()
    }

    @Transaction
    fun deleteAll(){
        deleteFrom()
        restartPrimaryKey()
    }

    @Query("DELETE FROM liste")
    fun deleteFrom()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'liste'")
    fun restartPrimaryKey()

    @Query("SELECT COUNT(id) FROM liste")
    fun count(): Int

    @Query("SELECT * FROM liste LIMIT 1")
    fun findFirstListe(): ListeResponse?
}