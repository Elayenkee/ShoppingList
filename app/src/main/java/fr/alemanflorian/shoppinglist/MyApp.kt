package fr.alemanflorian.shoppinglist

import android.app.Application
import fr.alemanflorian.shoppinglist.common.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(appModules)
        }
    }
}