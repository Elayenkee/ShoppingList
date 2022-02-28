package fr.alemanflorian.shoppinglist.data.api

import fr.alemanflorian.shoppinglist.data.model.ProductResultResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface RemoteApi {
    @GET("product/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode: String) : ProductResultResponse
}