package com.sabelnikova.vkdiscover.di

import com.google.gson.GsonBuilder
import com.sabelnikova.vkdiscover.api.ApiService
import com.sabelnikova.vkdiscover.api.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    companion object {
        private const val BASE_URL = "https://api.vk.com/"
    }

    @Singleton
    @Provides
    internal fun provideApi(): ApiService {
        return Retrofit.Builder()
                .client(OkHttpClient.Builder()
                        .build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                        .create()))
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create<ApiService>(ApiService::class.java)
    }
}
