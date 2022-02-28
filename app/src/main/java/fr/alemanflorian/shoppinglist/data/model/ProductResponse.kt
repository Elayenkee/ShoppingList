package fr.alemanflorian.shoppinglist.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import fr.alemanflorian.shoppinglist.domain.entity.Product

@Keep
@Entity(tableName = "product", indices = [Index(value = ["unique_name"], unique = true)])
data class ProductResponse(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "unique_name")
    val unique_name: String,
){
    fun toModel() = Product(id = id, pName = name)
}