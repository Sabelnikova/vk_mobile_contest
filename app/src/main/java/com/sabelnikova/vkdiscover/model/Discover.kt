package com.sabelnikova.vkdiscover.model

import com.google.gson.annotations.SerializedName

data class Discover(@SerializedName("items") val items: List<DiscoverItem>,
                    @SerializedName("next_from") val nextPage: String,
                    @SerializedName("profiles") val profiles: List<Profile>,
                    @SerializedName("groups") val groups: List<Group>)

data class DiscoverItem(@SerializedName("type") val type: String,
                        @SerializedName("post_id") val id: Long,
                        @SerializedName("source_id") val ownerId: Long,
                        @SerializedName("text") val text: String,
                        @SerializedName("date") val date: Long,
                        @SerializedName("attachments") val attachments: List<Attachment>?,
                        var owner: Owner?)

data class Attachment(@SerializedName("type") val type: String,
                      @SerializedName("photo") val photo: Photo?,
                      @SerializedName("video") val video: Video?)

data class Photo(@SerializedName("id") val id: Long,
                 @SerializedName("sizes") val sizes: List<PhotoSize>,
                 @SerializedName("access_key") val accessKey: String)

data class PhotoSize(@SerializedName("type") val type: String,
                     @SerializedName("url") val url: String,
                     @SerializedName("width") val width: Int,
                     @SerializedName("height") val height: Int)

data class Video(@SerializedName("id") val id: Long,
                 @SerializedName("photo_800") val url: String)