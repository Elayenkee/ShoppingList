package fr.alemanflorian.shoppinglist.presentation.listes.viewmodel

import androidx.lifecycle.*
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.ListeAvecProduits
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import fr.alemanflorian.shoppinglist.domain.usecase.UseCase
import fr.alemanflorian.shoppinglist.presentation.common.extension.contextIO

class ListesViewModel(private val useCase: UseCase): ViewModel() {

    //region Enregistre un nouveau produit depuis la recherche
    private class SaveNewProductParams(val product: Product)
    private val saveNewProductParams = MutableLiveData<SaveNewProductParams>()
    val saveNewProductResult: LiveData<Resource<Product>> = saveNewProductParams.switchMap {
            params -> useCase.saveProductAndAddToCurrentListe(params.product).asLiveData(contextIO())
    }
    fun saveNewProduct(product: Product) {
        saveNewProductParams.value = SaveNewProductParams(product)
    }
    //endregion

    //region Récupère la liste de tous les produits avec le nombre dans la liste affichée
    private val getAllProductsWithCurrentNbParams = MutableLiveData<GetAllProductsWithCurrentNbParams>()
    val getAllProductsWithCurrentNbResult: LiveData<Resource<List<ProductFromListe>>> = getAllProductsWithCurrentNbParams.switchMap {
        useCase.getAllProductsWithCurrentNb().asLiveData(contextIO())
    }
    fun getAllProductsWithCurrentNb() {
        getAllProductsWithCurrentNbParams.value = GetAllProductsWithCurrentNbParams()
    }
    private class GetAllProductsWithCurrentNbParams
    //endregion

    //region Incrémente le nombre du produit dans la liste affichée
    private class IncrementeProductParams(val product: ProductFromListe)
    private val incrementeProductParams = MutableLiveData<IncrementeProductParams>()
    val incrementeProductResult: LiveData<Resource<ProductFromListe>> = incrementeProductParams.switchMap {
        params -> useCase.incrementeProductToCurrentListe(params.product).asLiveData(contextIO())
    }
    fun incrementeProductToCurrentListe(product: ProductFromListe) {
        incrementeProductParams.value = IncrementeProductParams(product)
    }
    //endregion

    //region Décremente le nombre du produit dans la liste affichée
    private class DecrementeProductParams(val product: ProductFromListe)
    private val decrementeProductParams = MutableLiveData<DecrementeProductParams>()
    val decrementeProductResult: LiveData<Resource<Liste>> = decrementeProductParams.switchMap { params -> useCase.decrementeProductToCurrentListe(params.product).asLiveData(contextIO())}
    fun decrementeProductToCurrentListe(product: ProductFromListe) {decrementeProductParams.value = DecrementeProductParams(product)
    }
    //endregion

    //region Supprime le produit de la liste affichée
    private class DeleteProductFromCurrentListeParams(val product: ProductFromListe)
    private val deleteProductFromCurrentListeParams = MutableLiveData<DeleteProductFromCurrentListeParams>()
    val deleteProductFromCurrentListeResult: LiveData<Resource<Liste>> = deleteProductFromCurrentListeParams.switchMap { params -> useCase.deleteProductFromCurrentListe(params.product).asLiveData(contextIO())}
    fun deleteProductFromCurrentListe(product: ProductFromListe) {deleteProductFromCurrentListeParams.value = DeleteProductFromCurrentListeParams(product)
    }
    //endregion

    //region Récupère la liste à afficher
    private class GetCurrentListeParams
    private val getCurrentListeParams = MutableLiveData<GetCurrentListeParams>()
    val getCurrentListeResult: LiveData<Resource<ListeAvecProduits>> = getCurrentListeParams.switchMap {
        useCase.getCurrentListe().asLiveData(contextIO())
    }
    fun getCurrentListe(){getCurrentListeParams.value = GetCurrentListeParams()
    }
    //endregion

    //region Récupère toutes les listes
    private class GetAllListeParams
    private val getAllListeParams = MutableLiveData<GetAllListeParams>()
    val getAllListeResult: LiveData<Resource<List<Liste>>> = getAllListeParams.switchMap {
        useCase.getAllListes().asLiveData(contextIO())
    }
    fun getAllListe(){getAllListeParams.value = GetAllListeParams()
    }
    //endregion

    //region Enregistre une nouvelle liste
    private class SaveNewListeParams(val liste:Liste)
    private val saveNewListeParams = MutableLiveData<SaveNewListeParams>()
    val saveNewListeResult: LiveData<Resource<Liste>> = saveNewListeParams.switchMap {
        params -> useCase.saveNewListe(params.liste).asLiveData(contextIO())
    }
    fun saveNewListe(liste:Liste){saveNewListeParams.value = SaveNewListeParams(liste)}
    //endregion

    //region Set de la liste à afficher
    private class SetCurrentListeParams(val liste:Liste)
    private val setCurrentListeParams = MutableLiveData<SetCurrentListeParams>()
    val setCurrentListeResult: LiveData<Resource<Liste>> = setCurrentListeParams.switchMap { params -> useCase.saveCurrentListe(params.liste).asLiveData(contextIO()) }
    fun setCurrentListe(liste:Liste){setCurrentListeParams.value = SetCurrentListeParams(liste)
    }
    //endregion

    //region Supprime la liste
    private class DeleteListeParams(val liste: Liste)
    private val deleteListeParams = MutableLiveData<DeleteListeParams>()
    val deleteListeResult: LiveData<Resource<Long>> = deleteListeParams.switchMap { params -> useCase.deleteListe(params.liste).asLiveData(contextIO())}
    fun deleteListe(liste: Liste) {deleteListeParams.value = DeleteListeParams(liste)}
    //endregion

    suspend fun hasListeEnCours():Boolean{
        return useCase.hasListeEnCours()
    }

    suspend fun setCurrentListeAsListeEnCours(){
        useCase.setCurrentListeAsListeEnCours()
    }
}