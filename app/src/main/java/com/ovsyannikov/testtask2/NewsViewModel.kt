package com.ovsyannikov.testtask2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {

    val newsItemLiveData: LiveData<List<NewsItem>>

    init {
        newsItemLiveData = News().fetchNews()
    }

}