package com.sabelnikova.vkdiscover.ui

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.sabelnikova.vkdiscover.VkRepository
import com.sabelnikova.vkdiscover.api.ApiResponse
import com.sabelnikova.vkdiscover.model.Discover
import com.sabelnikova.vkdiscover.model.DiscoverItem
import javax.inject.Inject

class MainViewModel @Inject constructor(private val vkRepository: VkRepository): ViewModel() {

    val postsLiveData = MediatorLiveData<ApiResponse<Discover>>()
    get() {
        loadNext()
        return field
    }

    fun loadNext() = postsLiveData.addSource(vkRepository.getDiscover(postsLiveData.value?.body?.nextPage)){
        postsLiveData.value = it
    }

    fun userLoggedIn() = vkRepository.userLoggedIn()

    fun saveToken(token: String) = vkRepository.saveToken(token)

    fun like(discoverItem: DiscoverItem) =
            vkRepository.like(discoverItem.id, discoverItem.ownerId, discoverItem.type)

    fun ignore(discoverItem: DiscoverItem) =
            vkRepository.ignore(discoverItem.id, discoverItem.ownerId)
}