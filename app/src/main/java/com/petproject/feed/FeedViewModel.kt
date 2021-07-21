package com.petproject.feed

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.reactivex.disposables.CompositeDisposable

class FeedViewModel : ViewModel() {

    private val retrofitClientInstance = RetrofitClientInstance.instance

    var feedList: LiveData<PagedList<NewsFeedForListDto>>
    private val compositeDisposable = CompositeDisposable()
    private val pageSize = 50
    private var feedDataSourceFactory: FeedDataSourceFactory

    init {
        feedDataSourceFactory = FeedDataSourceFactory(retrofitClientInstance!!, compositeDisposable)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()

        feedList = LivePagedListBuilder<Int, NewsFeedForListDto>(feedDataSourceFactory, config)
            .build()

    }

    fun getState(): LiveData<State> =
        Transformations.switchMap<FeedDataSource, State>(
            feedDataSourceFactory.feedDataSourceLiveData,
            FeedDataSource::state
        )

    fun retry() {
        feedDataSourceFactory.feedDataSourceLiveData.value!!.retry()
    }

    fun refresh() {
        feedDataSourceFactory.feedDataSourceLiveData.value?.invalidate()
    }

    fun listIsEmpty(): Boolean {
        return if (feedList.value != null)
            feedList.value!!.isEmpty()
        else true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}


