package com.sabelnikova.vkdiscover.api

import android.arch.lifecycle.LiveData
import com.sabelnikova.vkdiscover.model.Discover
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("method/newsfeed.getDiscoverForContestant")
    fun getDiscoverForContestant(@Query("count") count: Int,
                                 @Query("start_from") from: String? = null,
                                 @Query("extended") extended: Int = 1,
                                 @Query("access_token") token: String,
                                 @Query("version") version: String = "5.87"):
            LiveData<ApiResponse<Discover>>

}