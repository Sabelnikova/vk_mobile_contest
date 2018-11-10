package com.sabelnikova.vkdiscover.api

import com.google.gson.annotations.SerializedName

class LikeResponse(@SerializedName("likes") val likesCount: Long)