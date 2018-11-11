package com.sabelnikova.vkdiscover

import com.sabelnikova.vkdiscover.di.DaggerAppComponent
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject


class App: DaggerApplication() {

    @Inject
    lateinit var vkRepository: VkRepository

    var vkAccessTokenTracker: VKAccessTokenTracker = object : VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            if (newToken == null) {
                vkRepository.clearToken()
            } else {
                vkRepository.saveToken(newToken.accessToken)
            }
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(this)
    }
}