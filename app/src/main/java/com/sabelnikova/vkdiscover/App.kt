package com.sabelnikova.vkdiscover

import com.sabelnikova.vkdiscover.di.DaggerAppComponent
import com.vk.sdk.VKSdk
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class App: DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(this)
    }
}