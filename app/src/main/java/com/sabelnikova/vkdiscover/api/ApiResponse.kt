package com.sabelnikova.vkdiscover.api

import android.support.v4.util.ArrayMap
import retrofit2.Response
import java.io.IOException
import java.util.regex.Pattern

class ApiResponse<T> {

    companion object {
        private val LINK_PATTERN = Pattern
                .compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
    }

    var code: Int
    var body: T?
    var error: ApiError? = null
    var links: MutableMap<String, String>

    constructor(throwable: Throwable) {
        code = 500
        body = null
        error = ApiError(code, throwable.message)
        links = mutableMapOf()
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            error = null
        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()!!.string()
                } catch (ignored: IOException) {
                }

            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            error = ApiError(code, message)
            body = null
        }
        val linkHeader = response.headers().get("link")
        if (linkHeader == null) {
            links = mutableMapOf()
        } else {
            links = ArrayMap()
            val matcher = LINK_PATTERN.matcher(linkHeader)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
        }
    }

    constructor(code: Int, body: T?, error: ApiError?) {
        this.code = code
        this.body = body
        this.error = error
        links = mutableMapOf()
    }

    val isSuccessful: Boolean
        get() = code in 200 until 300 && body != null
}
