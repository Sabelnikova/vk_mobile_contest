package com.sabelnikova.vkdiscover.di.android

import com.sabelnikova.vkdiscover.di.android.viewmodel.ViewModelModule
import com.sabelnikova.vkdiscover.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
interface ActivityModule {
    @ContributesAndroidInjector
    fun contributeMainActivity(): MainActivity
}