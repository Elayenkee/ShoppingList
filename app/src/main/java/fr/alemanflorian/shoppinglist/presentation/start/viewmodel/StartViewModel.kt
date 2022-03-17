package fr.alemanflorian.shoppinglist.presentation.start.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import fr.alemanflorian.shoppinglist.domain.usecase.UseCase

class StartViewModel(private val useCase: UseCase): ViewModel() {

    suspend fun startApp():NavDirections{
        return useCase.startApp()
    }

}