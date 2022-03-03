package com.standford.ligthhouse.api

import com.standford.ligthhouse.model.ApiData
import retrofit2.Call
import retrofit2.http.GET


interface APIInterface {

    @GET("json-data")
    fun doGetAllData(): Call<ApiData>

}