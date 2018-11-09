package com.sabelnikova.vkdiscover.ui

import android.arch.lifecycle.ViewModel
import com.sabelnikova.vkdiscover.VkRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(private val vkRepository: VkRepository): ViewModel() {

    fun getDiscover() = vkRepository.getDiscover()
}