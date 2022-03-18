package com.standford.ligthhouse.api

import com.standford.ligthhouse.model.ApiData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface APIInterface {

    @GET("json-data")
    fun doGetAllData(
        @Query("page") page: String,
        @Query("per_page") per_page: String
    ): Call<ApiData>

}