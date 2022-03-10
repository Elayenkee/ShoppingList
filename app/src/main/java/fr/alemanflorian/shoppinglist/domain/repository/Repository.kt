package fr.alemanflorian.shoppinglist.domain.repository

import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.Product

interface Repository
{
    fun onStart()

    fun setCachedProduct(product:Product)
    fun getCachedProduct(id:Long):Product?
    fun saveProduct(product:Product)
    fun getProduct(id:Long):Product?
    fun getAllProducts():List<Product>

    fun setCachedListe(liste:Liste)
    fun getCachedListe(id:Long):Liste?
    suspend fun saveListe(liste:Liste)
    fun getListe(id:Long):Liste?
    suspend fun getAllListes():List<Liste>
    suspend fun deleteListe(liste: Liste)

    suspend fun getCurrentListe():Liste
    fun saveCurrentListe(liste:Liste)

    suspend fun hasListeEnCours():Boolean
    suspend fun getListeEnCours():Liste?
    suspend fun finishListeEnCours()
    fun setListeEnCours(liste:Liste)
}