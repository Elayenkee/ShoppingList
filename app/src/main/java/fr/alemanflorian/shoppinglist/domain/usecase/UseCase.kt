package fr.alemanflorian.shoppinglist.domain.usecase

import android.content.res.Resources
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import fr.alemanflorian.shoppinglist.domain.repository.Repository
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UseCase (private val repository: Repository){

    fun onStart(){
        repository.onStart()
    }

    fun getProduct(id:Long) = flow<Resource<Product>>{
        val cachedProduct = repository.getCachedProduct(id)
        if(cachedProduct != null)
        {
            emit(Resource.success(cachedProduct))
        }
        else
        {
            val product = repository.getProduct(id) ?: throw Resources.NotFoundException()
            repository.setCachedProduct(product)
            emit(Resource.success(product))
        }
    }.catch{
        emit(Resource.Companion.failure(it))
    }

    fun getAllProducts() = flow<Resource<List<Product>>>
    {
        val allProducts = repository.getAllProducts()
        emit(Resource.success(allProducts))
    }.catch {
        emit(Resource.failure(it))
    }

    fun getAllProductsWithCurrentNb() = flow<Resource<List<ProductFromListe>>>
    {
        val liste = repository.getCurrentListe()
        val allProducts = repository.getAllProducts().map { ProductFromListe.create(it, liste.getNbOfProduct(it)) }
        emit(Resource.success(allProducts))
    }.catch {
        emit(Resource.failure(it))
    }

    fun saveProduct(product: Product) = flow<Resource<Product>> {
        emit(Resource.loading())
        repository.saveProduct(product)
        emit(Resource.success(product))
    }.catch {
        emit(Resource.failure(it, product))
    }

    fun getAllListes() = flow<Resource<List<Liste>>>
    {
        emit(Resource.success(repository.getAllListes()))
    }.catch {
        emit(Resource.failure(it))
    }

    fun addProductToCurrentListe(product: ProductFromListe) = flow<Resource<ProductFromListe>> {
        emit(Resource.loading())
        addToCurrentListe(product)
        emit(Resource.success(product))
    }.catch {
        emit(Resource.failure(it))
    }

    private suspend fun addToCurrentListe(product: ProductFromListe){
        val currentListe = repository.getCurrentListe();
        currentListe.incrementeProduct(product.product)
        repository.saveListe(currentListe)
        product.nb++
    }

    fun decrementeProductToCurrentListe(product: ProductFromListe) = flow<Resource<Liste>> {
        emit(Resource.loading())
        val currentListe = repository.getCurrentListe()
        currentListe.decrementeProduct(product.product)
        repository.saveListe(currentListe)
        product.nb--
        emit(Resource.success(currentListe))
    }.catch {
        emit(Resource.failure(it))
    }

    fun deleteProductFromCurrentListe(product: Product) = flow<Resource<Liste>> {
        emit(Resource.loading())
        val currentListe = repository.getCurrentListe();
        currentListe.deleteProduct(product)
        repository.saveListe(currentListe)
        emit(Resource.success(currentListe))
    }.catch {
        emit(Resource.failure(it))
    }

    fun getCurrentListe() = flow<Resource<Liste>> {
        emit(Resource.loading())
        emit(Resource.success(repository.getCurrentListe()))
    }.catch {
        emit(Resource.failure(it))
    }

    fun saveCurrentListe(liste:Liste) = flow<Resource<Liste>> {
        emit(Resource.loading())
        repository.saveCurrentListe(liste)
        emit(Resource.success(liste))
    }.catch {
        emit(Resource.failure(it))
    }

    fun saveNewListe(liste:Liste) = flow<Resource<Liste>> {
        emit(Resource.loading())
        repository.saveListe(liste)
        repository.saveCurrentListe(liste)
        emit(Resource.success(liste))
    }.catch {
        emit(Resource.failure(it))
    }

    suspend fun saveListe(liste:Liste){
        repository.saveListe(liste)
    }

    fun getProductsOfCurrentListe() = flow<Resource<List<ProductFromListe>>> {
        emit(Resource.loading())
        val liste = repository.getCurrentListe()
        val products = mutableListOf<ProductFromListe>()
        for(idProduct in liste.products.keys)
        {
            val product = repository.getProduct(idProduct)
            if(product != null)
                products.add(ProductFromListe.create(product, liste.getNbOfProduct(product)))
        }
        products.reverse()
        /*products.sortWith(object : Comparator<ProductFromListe>{
            override fun compare(o1: ProductFromListe?, o2: ProductFromListe?): Int {
                return o1!!.product.uniqueName.compareTo(o2!!.product.uniqueName)
            }
        })*/
        emit(Resource.success(products))
    }.catch {
        emit(Resource.failure(it))
    }

    fun deleteListe(liste: Liste) = flow<Resource<Long>>{
        emit(Resource.loading())
        repository.deleteListe(liste)
        emit(Resource.success(liste.id))
    }.catch {
        emit(Resource.failure(it))
    }

    fun saveProductAndAddToCurrentListe(product: Product) = flow<Resource<Product>> {
        emit(Resource.loading())
        repository.saveProduct(product)
        addToCurrentListe(ProductFromListe.create(product))
        emit(Resource.success(product))
    }.catch {
        emit(Resource.failure(it))
    }
}