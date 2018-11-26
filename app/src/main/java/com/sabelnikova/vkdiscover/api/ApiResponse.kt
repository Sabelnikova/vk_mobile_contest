package com.sabelnikova.vkdiscover.api

import retrofit2.Response
import java.io.IOException

class ApiResponse<T> {

    var code: Int
    var body: T?
    var error: ApiError? = null

    constructor(throwable: Throwable) {
        code = 500
        body = null
        error = ApiError(code, throwable.message)
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
                    message = response.errorBody()?.string()
                } catch (ignored: IOException) { }
            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            error = ApiError(code, message)
            body = null
        }
    }

    constructor(code: Int, body: T?, error: ApiError?) {
        this.code = code
        this.body = body
        this.error = error
    }

    val isSuccessful: Boolean
        get() = code in 200 until 300 && body != null
}
