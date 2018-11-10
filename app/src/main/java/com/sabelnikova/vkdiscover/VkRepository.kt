package com.sabelnikova.vkdiscover

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.SharedPreferences
import com.sabelnikova.vkdiscover.api.ApiResponse
import com.sabelnikova.vkdiscover.api.ApiService
import com.sabelnikova.vkdiscover.model.Discover
import javax.inject.Inject

class VkRepository @Inject constructor(private val apiService: ApiService,
                                       private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val USER_TOKEN = "user token"

        private const val PAGE_SIZE = 10
        private const val WALL_TYPE = "wall"
    }

    fun userLoggedIn(): Boolean = getToken() != ""

    fun getToken(): String = sharedPreferences.getString(USER_TOKEN, "")

    fun saveToken(token: String) = sharedPreferences.edit()
            .putString(USER_TOKEN, token)
            .apply()

    fun getDiscover(startFrom: String?): LiveData<ApiResponse<Discover>> =
            Transformations.map(apiService.getDiscoverForContestant(count = PAGE_SIZE, from = startFrom, token = getToken())) { response ->
                if (response.isSuccessful) {
                    response.body?.items?.forEach { item ->
                        item.owner = response.body?.groups?.find { it.id == -item.ownerId } ?: response.body?.profiles?.find { it.id == item.ownerId }
                    }
                }
                response
            }

    fun like(postId: Long, ownerId: Long, type: String) =
            apiService.like(type, ownerId, postId, getToken())

    fun ignore(postId: Long, ownerId: Long) = apiService.ignore(WALL_TYPE, ownerId, postId, getToken())
}