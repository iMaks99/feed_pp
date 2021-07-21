package com.petproject.feed

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable

class FeedNewPostDataSource(
    private val retrofitClientInstance: RetrofitClientInstance,
    private val compositeDisposable: CompositeDisposable
) {
    var state: MutableLiveData<State> = MutableLiveData()
    private var lastUpdate: Long = 0
}