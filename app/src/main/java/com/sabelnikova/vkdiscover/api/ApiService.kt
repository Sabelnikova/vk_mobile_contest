package com.sabelnikova.vkdiscover.api

import android.arch.lifecycle.LiveData
import com.sabelnikova.vkdiscover.model.Discover
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    companion object {
        const val API_VERSION = "5.87"
    }

    @GET("method/newsfeed.getDiscoverForContestant")
    fun getDiscoverForContestant(@Query("count") count: Int,
                                 @Query("start_from") from: String? = null,
                                 @Query("extended") extended: Int = 1,
                                 @Query("access_token") token: String,
                                 @Query("v") version: String = API_VERSION):
            LiveData<ApiResponse<Discover>>

    @GET("method/likes.add")
    fun like(@Query("type") type: String,
             @Query("owner_id") ownerId: Long,
             @Query("item_id") itemId: Long,
             @Query("access_token") token: String,
             @Query("v") version: String = API_VERSION):
            LiveData<ApiResponse<LikeResponse>>

    @GET("method/newsfeed.ignoreItem")
    fun ignore(@Query("type") type: String,
               @Query("owner_id") ownerId: Long,
               @Query("item_id") itemId: Long,
               @Query("access_token") token: String,
               @Query("v") version: String = API_VERSION): LiveData<ApiResponse<Int>>

}