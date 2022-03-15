package fr.alemanflorian.shoppinglist.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import fr.alemanflorian.shoppinglist.data.model.ListeResponse
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Liste(var id: Long = 0, var name: String, val products: LinkedHashMap<Long, Pair<Int, Int>>): Parcelable{
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
            products[product.id] = Pair(p.first + 1, 0)
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
            products[product.id] =  Pair(p.first - 1, 0)
        }

        if(products[product.id]!!.first == 0)
            deleteProduct(product)
    }

    fun onClickProduct(product: Product){
        if(product.id <= 0 || !products.containsKey(product.id) || products[product.id] == null)
            return

        val p = products.get(product.id)!!
        products[product.id] = Pair(p.first, 1 - p.second)
    }

    fun deleteProduct(product: Product){
        if(product.id > 0 && products.containsKey(product.id))
            products.remove(product.id)
    }

    fun getNbOfProduct(product: Product):Int{
        if(products.containsKey(product.id))
            return products.get(product.id)!!.first
        return 0
    }

    fun getCountOfProduct(product: Product):Int{
        if(products.containsKey(product.id))
            return products.get(product.id)!!.second
        return 0
    }

    fun isEmpty():Boolean = products.isEmpty()

    fun isFinished():Boolean{
        for(p in products.values)
        {
            if(p.second == 0)
                return false
        }
        return true
    }

    fun finish(){
        for(p in products)
            products[p.key] = Pair(p.value.first, 0)
    }
}