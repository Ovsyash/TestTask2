package com.ovsyannikov.testtask2

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class NewsItem(
    var author: String = "",
    var title: String = "",
    var description: String = "",
    var url: String = "",
    var urlToImage: String = "",
    @SerializedName("publishedAt") var data: String = ""
) {
    val pageUri: Uri
        get() {
            return Uri.parse(url)
        }
}
