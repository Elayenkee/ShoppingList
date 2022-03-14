package fr.alemanflorian.shoppinglist.presentation.product.viewmodel

import androidx.lifecycle.*
import fr.alemanflorian.shoppinglist.domain.entity.Product
import fr.alemanflorian.shoppinglist.domain.entity.ProductFromListe
import fr.alemanflorian.shoppinglist.domain.resource.Resource
import fr.alemanflorian.shoppinglist.domain.usecase.UseCase
import fr.alemanflorian.shoppinglist.presentation.common.extension.contextIO

class ProductViewModel(private val useCase: UseCase):ViewModel()
{
    private val saveProductParams = MutableLiveData<SaveProductParams>()
    val saveProductResult: LiveData<Resource<Product>> = saveProductParams.switchMap {
        params -> useCase.saveProduct(params.product).asLiveData(contextIO())
    }
    fun saveProduct(product: Product) {
        saveProductParams.value = SaveProductParams(product)
    }

    private class SaveNewProductParams(val product: Product)
    private val saveNewProductParams = MutableLiveData<SaveNewProductParams>()
    val saveNewProductResult: LiveData<Resource<Product>> = saveNewProductParams.switchMap {
        params -> useCase.saveProductAndAddToCurrentListe(params.product).asLiveData(contextIO())
    }
    fun saveNewProduct(product: Product) {
        saveNewProductParams.value = SaveNewProductParams(product)
    }

    private class SaveProductParams(val product: Product)

    private val getAllProductsParams = MutableLiveData<GetAllProductsParams>()
    val getAllProductsResult: LiveData<Resource<List<Product>>> = getAllProductsParams.switchMap { _ -> useCase.getAllProducts().asLiveData(contextIO())}
    fun getAllProducts() { getAllProductsParams.value = GetAllProductsParams()}
    private class GetAllProductsParams

    private val getAllProductsWithCurrentNbParams = MutableLiveData<GetAllProductsWithCurrentNbParams>()
    val getAllProductsWithCurrentNbResult: LiveData<Resource<List<ProductFromListe>>> = getAllProductsWithCurrentNbParams.switchMap {
        useCase.getAllProductsWithCurrentNb().asLiveData(contextIO())
    }
    fun getAllProductsWithCurrentNb() {
        getAllProductsWithCurrentNbParams.value = GetAllProductsWithCurrentNbParams()
    }
    private class GetAllProductsWithCurrentNbParams

    fun onStart(){
        useCase.onStart()
    }
}