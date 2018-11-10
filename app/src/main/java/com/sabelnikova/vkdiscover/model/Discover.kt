package com.sabelnikova.vkdiscover.model

import com.google.gson.annotations.SerializedName

data class Discover(@SerializedName("items") val items: List<DiscoverItem>,
                    @SerializedName("next_from") val nextPage: String)

data class DiscoverItem(@SerializedName("type") val type: String,
                        @SerializedName("post_id") val id: Long,
                        @SerializedName("source_id") val ownerId: Long,
                        @SerializedName("text") val text: String,
                        @SerializedName("date") val date: Long,
                        @SerializedName("attachments") val attachments: List<Attachment>)

data class Attachment(@SerializedName("type") val type: String,
                      @SerializedName("photo") val photo: Photo)

data class Photo(@SerializedName("id") val id: Long,
                 @SerializedName("sizes") val sizes: List<PhotoSize>,
                 @SerializedName("access_key") val accessKey: String)

data class PhotoSize(@SerializedName("type") val type: String,
                     @SerializedName("url") val url: String,
                     @SerializedName("width") val width: Int,
                     @SerializedName("height") val height: Int)