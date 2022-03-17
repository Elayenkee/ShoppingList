package fr.alemanflorian.shoppinglist.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import fr.alemanflorian.shoppinglist.domain.usecase.UseCase

class HomeViewModel(private val useCase: UseCase): ViewModel() {
    suspend fun clickShoppingApp():NavDirections{
        return useCase.clickShopping()
    }
}