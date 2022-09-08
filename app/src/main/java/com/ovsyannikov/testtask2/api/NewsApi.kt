package com.ovsyannikov.testtask2.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface NewsApi {

    @GET("v2/everything?q=Apple&from=2022-09-06&sortBy=popularity&" +
            "apiKey=985a31e628904aca82a828862f01faef")
    fun fetchNews(): Call<NewsResponse>

    @GET
    fun newsUrlBytes(@Url url: String): Call<ResponseBody>
}


//985a31e628904aca82a828862f01faef - api key