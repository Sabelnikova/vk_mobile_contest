package com.sabelnikova.vkdiscover.api

import com.google.gson.annotations.SerializedName

class VkResponse<T> (@SerializedName("response") val response: T,
                     @SerializedName("error") val error : ApiError)