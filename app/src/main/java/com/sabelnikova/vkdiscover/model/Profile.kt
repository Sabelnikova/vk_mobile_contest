package com.sabelnikova.vkdiscover.model

import com.google.gson.annotations.SerializedName

data class Profile(@SerializedName("id") val id: Long,
                   @SerializedName("last_name") val lastName: String,
                   @SerializedName("first_name") val firstName: String,
                   @SerializedName("photo_50") val photoUrl: String) : Owner {

    override fun getName(): String = "$firstName $lastName"

    override fun getPhoto(): String = photoUrl

}