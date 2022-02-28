package fr.alemanflorian.shoppinglist.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import fr.alemanflorian.shoppinglist.data.model.ListeResponse
import fr.alemanflorian.shoppinglist.domain.repository.Repository
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Liste(var id: Long, var name: String, val products: LinkedHashMap<Long, Pair<Int, Int>>): Parcelable{
    fun toResponse():ListeResponse{
        val strProducts = products.map {
            "${it.key},${it.value.first},${it.value.second}"
        }.joinToString(";")
        return ListeResponse(id, name, strProducts)
    }

    fun incrementeProduct(product: Product){
        if(product.id <= 0)
            return

        if(products.containsKey(product.id) && products[product.id] != null)
        {
            val p = products.get(product.id)!!
            products[product.id] = Pair(p.first + 1, p.second)
        }
        else
            products.put(product.id, Pair(1, 0))
    }

    fun decrementeProduct(product: Product){
        if(product.id <= 0)
            return

        if(products.containsKey(product.id) && products[product.id] != null)
        {
            val p = products.get(product.id)!!
            products[product.id] =  Pair(p.first - 1, p.second)
        }

        if(products[product.id]!!.first == 0)
            deleteProduct(product)
    }

    fun deleteProduct(product: Product){
        if(product.id > 0 && products.containsKey(product.id))
            products.remove(product.id)
    }

    fun getNbOfProduct(product: Product):Int
    {
        if(products.containsKey(product.id))
            return products.get(product.id)!!.first
        return 0
    }

    companion object
    {
        fun from(name : String, mapProducts: LinkedHashMap<Product, Int>):Liste
        {
            return from(0, name, mapProducts)
        }

        fun from(id : Long, name : String, mapProducts: LinkedHashMap<Product, Int>):Liste
        {
            val products = LinkedHashMap<Long, Pair<Int,Int>>()
            mapProducts.forEach {
                products.put(it.key.id, Pair(it.value, 0))
            }
            return Liste(id, name, products)
        }
    }
}