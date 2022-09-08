package com.ovsyannikov.testtask2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ovsyannikov.testtask2.api.NewsApi
import com.ovsyannikov.testtask2.api.NewsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "News"

class News {

    private val newsApi: NewsApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        newsApi = retrofit.create(NewsApi::class.java)
    }

    fun fetchNews() : LiveData<List<NewsItem>> {
        val responseLiveData: MutableLiveData<List<NewsItem>> = MutableLiveData()
        val newsRequest: Call<NewsResponse> = newsApi.fetchNews()

        newsRequest.enqueue(object : Callback<NewsResponse> {

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e(TAG, "Failed to news", t)
            }

            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                Log.d(TAG, "Response received")
                val newsResponse: NewsResponse? = response.body()
                val newsItems: List<NewsItem> = newsResponse?.newsItems
                    ?: mutableListOf()

                responseLiveData.value = newsItems
            }
        })

        return responseLiveData
    }

    @WorkerThread
    fun newsPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = newsApi.newsUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoder bitmap=$bitmap from Response=$response")
        return bitmap
    }
}