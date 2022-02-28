package fr.alemanflorian.shoppinglist.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import fr.alemanflorian.shoppinglist.common.extension.unaccent
import fr.alemanflorian.shoppinglist.data.model.ProductResponse
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Product(var id: Long, var pName: String): Parcelable{
    val name = pName.capitalize()
    val uniqueName = name.toLowerCase().unaccent()
    fun toResponse():ProductResponse {
        return ProductResponse(id = id, name = name, unique_name = uniqueName)
    }
}