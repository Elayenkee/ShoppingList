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

    private class AddProductParams(val product: ProductFromListe)
    private val addProductParams = MutableLiveData<AddProductParams>()
    val addProductResult: LiveData<Resource<ProductFromListe>> = addProductParams.switchMap {
        params -> useCase.addProductToCurrentListe(params.product).asLiveData(contextIO())
    }
    fun addProductToCurrentListe(product: ProductFromListe) {
        addProductParams.value = AddProductParams(product)
    }

    private class DecrementeProductParams(val product: ProductFromListe)
    private val decrementeProductParams = MutableLiveData<DecrementeProductParams>()
    val decrementeProductResult: LiveData<Resource<Liste>> = decrementeProductParams.switchMap { params -> useCase.decrementeProductToCurrentListe(params.product).asLiveData(contextIO())}
    fun decrementeProductToCurrentListe(product: ProductFromListe) {decrementeProductParams.value = DecrementeProductParams(product)
    }

    private class DeleteProductFromCurrentListeParams(val product: ProductFromListe)
    private val deleteProductFromCurrentListeParams = MutableLiveData<DeleteProductFromCurrentListeParams>()
    val deleteProductFromCurrentListeResult: LiveData<Resource<Liste>> = deleteProductFromCurrentListeParams.switchMap { params -> useCase.deleteProductFromCurrentListe(params.product).asLiveData(contextIO())}
    fun deleteProductFromCurrentListe(product: ProductFromListe) {deleteProductFromCurrentListeParams.value = DeleteProductFromCurrentListeParams(product)
    }

    private class GetCurrentListeParams
    private val getCurrentListeParams = MutableLiveData<GetCurrentListeParams>()
    val getCurrentListeResult: LiveData<Resource<ListeAvecProduits>> = getCurrentListeParams.switchMap {
        useCase.getCurrentListe().asLiveData(contextIO())
    }
    fun getCurrentListe(){getCurrentListeParams.value = GetCurrentListeParams()
    }

    private class GetAllListeParams
    private val getAllListeParams = MutableLiveData<GetAllListeParams>()
    val getAllListeResult: LiveData<Resource<List<Liste>>> = getAllListeParams.switchMap {
        useCase.getAllListes().asLiveData(contextIO())
    }
    fun getAllListe(){getAllListeParams.value = GetAllListeParams()
    }

    private class SaveNewListeParams(val liste:Liste)
    private val saveNewListeParams = MutableLiveData<SaveNewListeParams>()
    val saveNewListeResult: LiveData<Resource<Liste>> = saveNewListeParams.switchMap {
        params -> useCase.saveNewListe(params.liste).asLiveData(contextIO())
    }
    fun saveNewListe(liste:Liste){saveNewListeParams.value = SaveNewListeParams(liste)}

    private class SaveCurrentListeParams(val liste:Liste)
    private val saveCurrentListeParams = MutableLiveData<SaveCurrentListeParams>()
    val saveCurrentListeResult: LiveData<Resource<Liste>> = saveCurrentListeParams.switchMap { params -> useCase.saveCurrentListe(params.liste).asLiveData(contextIO()) }
    fun saveCurrentListe(liste:Liste){saveCurrentListeParams.value = SaveCurrentListeParams(liste)
    }

    private class DeleteListeParams(val liste: Liste)
    private val deleteListeParams = MutableLiveData<DeleteListeParams>()
    val deleteListeResult: LiveData<Resource<Long>> = deleteListeParams.switchMap { params -> useCase.deleteListe(params.liste).asLiveData(contextIO())}
    fun deleteListe(liste: Liste) {deleteListeParams.value = DeleteListeParams(liste)}

    suspend fun hasListeEnCours():Boolean{
        return useCase.hasListeEnCours()
    }

    suspend fun setCurrentListeAsListeEnCours(){
        useCase.setCurrentListeAsListeEnCours()
    }
}