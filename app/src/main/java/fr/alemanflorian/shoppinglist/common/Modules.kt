package fr.alemanflorian.shoppinglist.common

import fr.alemanflorian.shoppinglist.data.api.RemoteApi
import fr.alemanflorian.shoppinglist.data.database.createDatabase
import fr.alemanflorian.shoppinglist.data.database.createListeDao
import fr.alemanflorian.shoppinglist.data.database.createProductDao
import fr.alemanflorian.shoppinglist.data.network.createApiClient
import fr.alemanflorian.shoppinglist.data.repository.RepositoryImpl
import fr.alemanflorian.shoppinglist.domain.repository.Repository
import fr.alemanflorian.shoppinglist.domain.usecase.UseCase
import fr.alemanflorian.shoppinglist.presentation.listes.viewmodel.ListesViewModel
import fr.alemanflorian.shoppinglist.presentation.product.viewmodel.ProductViewModel
import fr.alemanflorian.shoppinglist.presentation.shopping.viewmodel.ShoppingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val appModules by lazy {
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        dataModule
    )
}

val viewModelModule: Module = module {
    viewModel {ProductViewModel(useCase = get())}
    viewModel { ListesViewModel(useCase = get()) }
    viewModel { ShoppingViewModel(useCase = get()) }
}

val useCaseModule: Module = module {
    single {
        val repository : Repository = get()
        UseCase(repository)
    }
}

val repositoryModule: Module = module {
    single {
        RepositoryImpl(productDao = get(), listeDao = get(), appContext = get()) as Repository
    }
}

val dataModule: Module = module {
    single { createApiClient().create(RemoteApi::class.java) }
    single { createDatabase(appContext = get()) }
    single { createProductDao(database = get()) }
    single { createListeDao(database = get()) }
}