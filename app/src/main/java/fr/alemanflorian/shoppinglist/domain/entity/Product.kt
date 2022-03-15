package fr.alemanflorian.shoppinglist.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import fr.alemanflorian.shoppinglist.common.extension.unaccent
import fr.alemanflorian.shoppinglist.data.model.ProductResponse
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class Product(var id: Long = 0, val name:String, val uniqueName:String): Parcelable{

    fun toResponse():ProductResponse {
        return ProductResponse(id, name, uniqueName)
    }

    constructor(id:Long = 0, name: String):this(id, name.capitalize(), name.toLowerCase().unaccent())
}