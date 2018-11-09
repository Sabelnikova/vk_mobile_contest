package com.sabelnikova.vkdiscover.di

import com.sabelnikova.vkdiscover.App
import com.sabelnikova.vkdiscover.di.android.ActivityModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityModule::class,
    NetworkModule::class])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()

    override fun inject(app: App)

}