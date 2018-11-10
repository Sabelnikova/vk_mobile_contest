package com.sabelnikova.vkdiscover.model

import com.google.gson.annotations.SerializedName

data class Group(@SerializedName("id") val id: Long,
                 @SerializedName("name") val groupName: String,
                 @SerializedName("photo_50") val photoUrl: String) : Owner {

    override fun getName(): String = groupName

    override fun getPhoto(): String = photoUrl

}