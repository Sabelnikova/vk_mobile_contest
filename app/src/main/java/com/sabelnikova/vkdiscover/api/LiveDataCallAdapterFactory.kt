package com.sabelnikova.vkdiscover.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.google.gson.Gson
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean


class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        if (CallAdapter.Factory.getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = CallAdapter.Factory.getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = CallAdapter.Factory.getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val bodyType = CallAdapter.Factory.getParameterUpperBound(0, observableType)

        return LiveDataCallAdapter<Any>(bodyType)
    }
}

class LiveDataCallAdapter<R>(val resType: Type) : CallAdapter<VkResponse<R>, LiveData<ApiResponse<R>>> {

    override fun responseType(): Type {
        return VkResponse::class.java
    }

    override fun adapt(call: Call<VkResponse<R>>): LiveData<ApiResponse<R>> {
        return Transformations.map(object : LiveData<ApiResponse<VkResponse<R>>>() {
            var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<VkResponse<R>> {
                        override fun onResponse(call: Call<VkResponse<R>>, response: Response<VkResponse<R>>) {
                            postValue(ApiResponse(response))
                        }

                        override fun onFailure(call: Call<VkResponse<R>>, throwable: Throwable) {
                            postValue(ApiResponse(throwable))
                        }
                    })
                }
            }
        }) {
            val body: R?
            val error: ApiError?
            val code: Int = it.code
            if (it.isSuccessful) {
                if (it.body?.response != null) {
                    val gson = Gson()
                    val json = gson.toJson(it.body?.response)
                    body = gson.fromJson(json, resType)
                    error = null
                } else {
                    body = null
                    error = it.body?.error
                }
            } else {
                body = null
                error = it.error
            }
            ApiResponse(code, body, error)
        }
    }
}
