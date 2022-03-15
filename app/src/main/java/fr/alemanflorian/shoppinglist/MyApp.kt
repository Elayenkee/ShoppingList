package fr.alemanflorian.shoppinglist

import android.app.Application
import com.google.firebase.FirebaseApp
import fr.alemanflorian.shoppinglist.common.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        startKoin {
            androidContext(applicationContext)
            modules(appModules)
        }
    }
}