package fr.alemanflorian.shoppinglist.domain.usecase

import android.content.res.Resources
import fr.alemanflorian.shoppinglist.domain.entity.*
import fr.alemanflorian.shoppinglist.domain.repository.Repository
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import fr.alemanflorian.shoppinglist.presentation.shopping.viewmodel.ShoppingViewModel
import fr.alemanflorian.shoppinglist.presentation.shopping.viewmodel.ShoppingViewModel.InitListeEnCoursResult
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UseCase (private val repository: Repository){

    fun onStart(){
        repository.onStart()
    }

    suspend fun hasListes():Boolean{
        val listes = repository.getAllListes();
        for(liste in listes)
        {
            if(!liste.isEmpty())
                return true
        }
        return false
    }

    suspend fun hasListeEnCours():Boolean{
        return repository.hasListeEnCours()
    }

    suspend fun finishListeEnCours(){
        repository.finishListeEnCours()
    }

    fun setListeEnCours(liste: Liste){
        repository.setListeEnCours(liste)
    }

    suspend fun setCurrentListeAsListeEnCours(){
        val currentListe = repository.getCurrentListe()
        repository.setListeEnCours(currentListe)
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
        emit(Resource.failure(it))
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

    fun saveProduct(product: Product) = flow{
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

    fun addProductToCurrentListe(product: ProductFromListe) = flow{
        emit(Resource.loading())
        addToCurrentListe(product)
        emit(Resource.success(product))
    }.catch {
        emit(Resource.failure(it))
    }

    private suspend fun addToCurrentListe(product: ProductFromListe){
        val currentListe = repository.getCurrentListe()
        currentListe.incrementeProduct(product.product)
        repository.saveListe(currentListe)
        product.nb++
    }

    fun decrementeProductToCurrentListe(product: ProductFromListe) = flow{
        emit(Resource.loading())
        val currentListe = repository.getCurrentListe()
        currentListe.decrementeProduct(product.product)
        repository.saveListe(currentListe)
        product.nb--
        emit(Resource.success(currentListe))
    }.catch {
        emit(Resource.failure(it))
    }

    fun deleteProductFromCurrentListe(product: ProductFromListe) = flow{
        emit(Resource.loading())
        val currentListe = repository.getCurrentListe()
        currentListe.deleteProduct(product.product)
        repository.saveListe(currentListe)
        emit(Resource.success(currentListe))
    }.catch {
        emit(Resource.failure(it))
    }

    fun getCurrentListe() = flow{
        emit(Resource.loading())
        val liste = repository.getCurrentListe()
        emit(Resource.success(ListeAvecProduits(repository.getCurrentListe(), repository)))
    }.catch {
        emit(Resource.failure(it))
    }

    fun saveCurrentListe(liste:Liste) = flow{
        emit(Resource.loading())
        repository.saveCurrentListe(liste)
        emit(Resource.success(liste))
    }.catch {
        emit(Resource.failure(it))
    }

    fun saveNewListe(liste:Liste) = flow{
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

    /*fun getProductsOfCurrentListe() = flow<Resource<List<ProductFromListe>>> {
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
        emit(Resource.success(products))
    }.catch {
        emit(Resource.failure(it))
    }*/

    fun deleteListe(liste: Liste) = flow{
        emit(Resource.loading())
        repository.deleteListe(liste)
        emit(Resource.success(liste.id))
    }.catch {
        emit(Resource.failure(it))
    }

    fun saveProductAndAddToCurrentListe(product: Product) = flow{
        emit(Resource.loading())
        repository.saveProduct(product)
        addToCurrentListe(ProductFromListe.create(product))
        emit(Resource.success(product))
    }.catch {
        emit(Resource.failure(it))
    }

    fun getListeEnCours() = flow{
        emit(Resource.loading())
        emit(Resource.success(repository.getListeEnCours()))
    }.catch {
        emit(Resource.failure(it))
    }

    fun initListeEnCours() = flow<Resource<InitListeEnCoursResult>>{
        emit(Resource.loading())
        var liste:Liste? = repository.getListeEnCours()
        if(liste == null)
        {
            val listes = repository.getAllListes()
            if(listes.size == 1)
            {
                liste = listes[0]
                repository.setListeEnCours(liste)
            }
        }

        if(liste != null)
        {
            val result = ListeEnCours(liste, repository)
            emit(Resource.success(InitListeEnCoursResult(result, ArrayList())))
        }
        else
        {
            emit(Resource.success(InitListeEnCoursResult(null, repository.getAllListes())))
        }
    }.catch {
        emit(Resource.failure(it))
    }

    fun clickProductEnCours(product: ProductFromListe) = flow<Resource<ShoppingViewModel.ClickProductResult>>{
        emit(Resource.loading())
        val listeEnCours = repository.getListeEnCours()!!
        listeEnCours.onClickProduct(product.product)
        repository.saveListe(listeEnCours)
        product.count = listeEnCours.getCountOfProduct(product.product)
        val isFinished = listeEnCours.isFinished()
        emit(Resource.success(ShoppingViewModel.ClickProductResult(product, isFinished)))

    }.catch {
        emit(Resource.failure(it))
    }
}