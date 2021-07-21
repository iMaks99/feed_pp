package com.petproject.feed

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.reactivex.disposables.CompositeDisposable

class FeedDataSourceFactory(
    private val retrofitClientInstance: RetrofitClientInstance,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, NewsFeedForListDto>() {

    val feedDataSourceLiveData = MutableLiveData<FeedDataSource>()

    override fun create(): DataSource<Int, NewsFeedForListDto> {
        val feedDataSource = FeedDataSource(retrofitClientInstance, compositeDisposable)
        feedDataSourceLiveData.postValue(feedDataSource)
        return feedDataSource
    }
}