package com.ovsyannikov.testtask2

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ovsyannikov.testtask2.databinding.NewsFragmentBinding
import com.ovsyannikov.testtask2.databinding.NewsItemBinding

private const val TAG = "NewsFragment"

class NewsFragment : Fragment() {

    private lateinit var binding: NewsFragmentBinding
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var thumbnailDownloader: ThumbnailDownloader<NewsHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        newsViewModel = ViewModelProviders.of(this)[NewsViewModel::class.java]

        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) {newsHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            newsHolder.bindingClass.imageView.setImageDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
        binding = NewsFragmentBinding.inflate(inflater)
        newsRecyclerView = binding.newsRecyclerView
        newsRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel.newsItemLiveData.observe(
            viewLifecycleOwner
        ) { newsItems ->
            newsRecyclerView.adapter = NewsAdapter(newsItems)
        }
    }

    private inner class NewsHolder(item: View) : RecyclerView.ViewHolder(item),
        View.OnClickListener {

        val bindingClass = NewsItemBinding.bind(item)
        private lateinit var newsItem: NewsItem

        fun bind(news: NewsItem) = with(bindingClass) {
            title.text = news.title
            author.text = news.author
            data.text = news.data
            description.text = news.description
        }

        fun bindNewsItem(item: NewsItem) {
            newsItem = item
        }

        override fun onClick(view: View) {
            val intent = Intent(Intent.ACTION_VIEW, newsItem.pageUri)
            startActivity(intent)
        }

        init {
            item.setOnClickListener(this)
        }
    }

    private inner class NewsAdapter(private val newsItems: List<NewsItem>)
        : RecyclerView.Adapter<NewsHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NewsHolder {
            val view = layoutInflater.inflate(R.layout.news_item,
            parent,
            false)
            return NewsHolder(view)
        }

        override fun onBindViewHolder(holder: NewsHolder, position: Int) {
            holder.bind(newsItems[position])
            holder.bindNewsItem(newsItems[position])
            thumbnailDownloader.queueThumbnail(holder, newsItems[position].urlToImage)
        }

        override fun getItemCount(): Int = newsItems.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            thumbnailDownloader.fragmentLifecycleObserver
        )
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}