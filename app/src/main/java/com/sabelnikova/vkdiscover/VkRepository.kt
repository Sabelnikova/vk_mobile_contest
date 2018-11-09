package com.sabelnikova.vkdiscover

import com.sabelnikova.vkdiscover.api.ApiService
import javax.inject.Inject

class VkRepository @Inject constructor(private val apiService: ApiService){

    fun getDiscover() = apiService.getDiscoverForContestant(count = 10, token = "8ce872600f46f9aff73ffbf643fd7803898012b15ab1e77e910dcdac86deda9da92a293824d7cd730387a")
}