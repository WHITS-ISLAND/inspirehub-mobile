package io.github.witsisland.inspirehub

import android.app.Application
import io.github.witsisland.inspirehub.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InspireHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@InspireHubApplication)
            modules(appModule)
        }
    }
}
