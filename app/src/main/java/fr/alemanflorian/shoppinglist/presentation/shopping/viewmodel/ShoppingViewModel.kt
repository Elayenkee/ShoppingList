package fr.alemanflorian.shoppinglist.presentation.shopping.viewmodel

import androidx.lifecycle.*
import fr.alemanflorian.shoppinglist.domain.entity.Liste
import fr.alemanflorian.shoppinglist.domain.entity.ListeEnCours
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import fr.alemanflorian.shoppinglist.domain.usecase.UseCase
import fr.alemanflorian.shoppinglist.presentation.common.extension.contextIO

class ShoppingViewModel(private val useCase: UseCase): ViewModel() {

    private class GetListeEnCoursParams
    private val getListeEnCoursParams = MutableLiveData<GetListeEnCoursParams>()
    val getListeEnCoursResult: LiveData<Resource<Liste?>> = getListeEnCoursParams.switchMap {
        useCase.getListeEnCours().asLiveData(contextIO())
    }
    fun getListeEnCours(){getListeEnCoursParams.value = GetListeEnCoursParams()
    }

    suspend fun hasListes():Boolean{
        return useCase.hasListes()
    }

    suspend fun hasListeEnCours():Boolean{
        return useCase.hasListeEnCours()
    }

    private class InitListeEnCoursParams
    private val initListeEnCoursParams = MutableLiveData<InitListeEnCoursParams>()
    val initListeEnCoursResult: LiveData<Resource<InitListeEnCoursResult>> = initListeEnCoursParams.switchMap {
        useCase.initListeEnCours().asLiveData(contextIO())
    }
    fun initListeEnCours(){initListeEnCoursParams.value = InitListeEnCoursParams()
    }
    class InitListeEnCoursResult(val listeEnCours: ListeEnCours?, val listes:List<Liste>)

    private class ClickProductParams(val product: ProductFromListe)
    private val clickProductParams = MutableLiveData<ClickProductParams>()
    val clickProductResult: LiveData<Resource<ClickProductResult>> = clickProductParams.switchMap { params -> useCase.clickProductEnCours(params.product).asLiveData(contextIO())}
    fun clickProduct(product: ProductFromListe)
    {
        clickProductParams.value = ClickProductParams(product)
    }

    class ClickProductResult(val product: ProductFromListe, val listeFinished:Boolean)

    suspend fun finishListeEnCours(){
        useCase.finishListeEnCours()
    }

    fun setListeEnCours(liste:Liste){
        useCase.setListeEnCours(liste)
    }
}