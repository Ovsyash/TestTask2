package com.ovsyannikov.testtask2.api

import com.google.gson.annotations.SerializedName
import com.ovsyannikov.testtask2.NewsItem

class NewsResponse {
    @SerializedName("articles")
    lateinit var newsItems: List<NewsItem>
}