package com.sabelnikova.vkdiscover.api

import com.google.gson.annotations.SerializedName

data class ApiError(@SerializedName("error_code") val code: Int?,
               @SerializedName("error_msg") val message: String?)