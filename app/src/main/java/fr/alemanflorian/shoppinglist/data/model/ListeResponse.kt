package fr.alemanflorian.shoppinglist.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.alemanflorian.shoppinglist.domain.entity.Liste

@Keep
@Entity(tableName = "liste")
data class ListeResponse(
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "products")
    val products: String
){
    fun toModel():Liste
    {
        val mapProducts = LinkedHashMap<Long, Pair<Int, Int>>()
        try{
            val tab = products.split(";")
            tab.forEach {
                val split = it.split(",")
                val ok = if(split.size > 2) split[2].toInt() else 0
                mapProducts.put(split[0].toLong(), Pair(split[1].toInt(), ok))
            }
        }catch (e:Exception){

        }
        return Liste(id = id, name = name, products = mapProducts)
    }
}