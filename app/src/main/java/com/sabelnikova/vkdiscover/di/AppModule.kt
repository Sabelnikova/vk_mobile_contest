package com.sabelnikova.vkdiscover.di

import android.content.Context
import android.content.SharedPreferences
import com.sabelnikova.vkdiscover.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    companion object {
        const val APP_PREFERENCES = "Vk settings"
    }

    @Singleton
    @Provides
    fun provideContext(app: App): Context = app.applicationContext

    @Singleton
    @Provides
    fun provideDefaultSharedPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
}