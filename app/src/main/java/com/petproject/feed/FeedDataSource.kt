package com.petproject.feed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FeedDataSource(
    private val retrofitClientInstance: RetrofitClientInstance,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, NewsFeedForListDto>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var feed: ArrayList<NewsFeedForListDto> = ArrayList()
    private var feedLoadAfter: ArrayList<NewsFeedForListDto> = ArrayList()
    private var retryCompletable: Completable? = null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, NewsFeedForListDto>
    ) {
        compositeDisposable.add(
            retrofitClientInstance.feedEndpoint!!.feedList(
                MainActivity.pref.getString("accessToken", "")!!,
                1
            ).subscribe(
                { response ->
                    updateState(State.DONE)

                    feed = response.data

                    feed.sortByDescending {
                        when (it.itemType) {
                            ItemType.PRODUCT_SELECTION -> (it.getNewsFeedItem().pubTime * 1000)
                            ItemType.WP_POST -> toLocalTime(it.getNewsFeedItem().pubTime)
                            ItemType.COMPETITION -> (it.getNewsFeedItem().pubTime * 1000)
                            ItemType.LIVE_STREAM -> (it.getNewsFeedItem().pubTime * 1000)
                            ItemType.PODCAST -> (it.getNewsFeedItem().pubTime * 1000)
                            else -> it.getNewsFeedItem().pubTime
                        }
                    }


                    val tempLiveStreams = ArrayList<NewsFeedForListDto>()
                    val tempPlannedStream = ArrayList<NewsFeedForListDto>()
                    val feedIt = feed.iterator()
                    for (i in feedIt) {
                        if (i.itemType == ItemType.LIVE_STREAM) {
                            if ((i.getNewsFeedItem() as LivePostItem).status == LiveStatus.PLANNED) {
                                tempPlannedStream.add(i)
                                feedIt.remove()
                            } else if((i.getNewsFeedItem() as LivePostItem).status == LiveStatus.LIVE)
                            {
                                tempLiveStreams.add(i)
                                feedIt.remove()
                            }
                        }
                    }

                    for(i in tempPlannedStream)
                        feed.add(0, i)

                    for(i in tempLiveStreams)
                        feed.add(0, i)

                    checkSwitchPosition()

                    val it = feed.iterator()

                    for (i in it) {

                        if (i.itemType.toString() == "VIDEO" && !MainActivity.switchForVideo)
                            it.remove()
                        if (i.itemType.toString() == "PODCAST" && !MainActivity.switchForPodcasts)
                            it.remove()
                        if (i.itemType.toString() == "PRODUCT_SELECTION" && !MainActivity.switchForCollections)
                            it.remove()
                        if (i.itemType.toString() == "INSTAGRAM_POST" && !MainActivity.switchForPostFromSocialNetwork)
                            it.remove()
                        if (i.itemType.toString() == "TWITTER_POST" && !MainActivity.switchForPostFromSocialNetwork)
                            it.remove()

                    }

//                    if (!MainActivity.pref.getBoolean("isRateAppVisible", false)) {
//                        if (compareRateAppDateWithCurrent()) {
//                            feed.add(9, NewsFeedForListDto(ItemType.RATE_APP, LinkedTreeMap()))
//                            MainActivity.pref.edit().putBoolean("isRateAppVisible", true).apply()
//                        }
//                    } else {
//                        MainActivity.pref.edit().putBoolean("isRateAppVisible", false).apply()
//                    }

                    callback.onResult(
                        feed,
                        null,
                        2
                    )
                },
                {
                    updateState(State.LOADING)
                    setRetry(Action { loadInitial(params, callback) })
                    updateToken(null, null, params, callback)
                }
            )
        )
    }


    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NewsFeedForListDto>
    ) {
        compositeDisposable.add(
            retrofitClientInstance.feedEndpoint!!.feedList(
                MainActivity.pref.getString("accessToken", "")!!,
                params.key
            ).subscribe(
                { response ->

                    feedLoadAfter = response.data
                    checkSwitchPosition()

                    val it = feedLoadAfter.iterator()

                    for (i in it) {

                        if (i.itemType.toString() == "VIDEO" && !MainActivity.switchForVideo)
                            it.remove()
                        if (i.itemType.toString() == "PODCAST" && !MainActivity.switchForPodcasts)
                            it.remove()
                        if (i.itemType.toString() == "PRODUCT_SELECTION" && !MainActivity.switchForCollections)
                            it.remove()
                        if (i.itemType.toString() == "INSTAGRAM_POST" && !MainActivity.switchForPostFromSocialNetwork)
                            it.remove()
                        if (i.itemType.toString() == "TWITTER_POST" && !MainActivity.switchForPostFromSocialNetwork)
                            it.remove()

                    }

                    updateState(State.DONE)
                    callback.onResult(
                        feedLoadAfter,
                        params.key + 1
                    )
                },
                {
                    updateState(State.LOADING)
                    setRetry(Action { loadAfter(params, callback); })
                    updateToken(params, callback, null, null)
                }
            )

        )
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NewsFeedForListDto>
    ) {
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    private fun setRetry(action: Action?) {

        retryCompletable =
            if (action == null) null
            else Completable.fromAction(action)
    }

    private fun updateToken(
        params: LoadParams<Int>?,
        callback: LoadCallback<Int, NewsFeedForListDto>?,
        paramsInit: LoadInitialParams<Int>?,
        callbackInit: LoadInitialCallback<Int, NewsFeedForListDto>?
    ) {
        retrofitClientInstance.authenticationEndpoint!!.refresh(
            MobileAuthenticationRefreshRequest(
                MainActivity.pref.getString(
                    "refreshToken",
                    ""
                )!!
            )
        ).enqueue(object : Callback<MobileAuthResponseDto> {
            override fun onFailure(call: Call<MobileAuthResponseDto>, t: Throwable) {
                Log.w(this::class.java.name, t.localizedMessage!!)
                updateState(State.ERROR)
            }

            override fun onResponse(
                call: Call<MobileAuthResponseDto>,
                response: Response<MobileAuthResponseDto>
            ) {
                if (response.isSuccessful) {
                    response.body()!!.accessToken
                    MainActivity.pref.edit()
                        .putString("accessToken", response.body()!!.accessToken)
                        .apply()

                    MainActivity.pref.edit()
                        .putString("refreshToken", response.body()!!.refreshToken)
                        .apply()

                    if (params != null && callback != null)
                        invalidate()
                    else if (paramsInit != null && callbackInit != null)
                        invalidate()
                } else {
                    Log.w(this::class.java.name, response.errorBody().toString())
                    updateState(State.ERROR)
                }
            }
        })

    }

    fun retry() {

        if (retryCompletable != null) {
            compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            )
        }
    }

    private fun checkSwitchPosition() {

        MainActivity.switchForVideo =
            MainActivity.pref.getBoolean("switch_for_video", true)

        MainActivity.switchForContests =
            MainActivity.pref.getBoolean("switch_for_contests", true)

        MainActivity.switchForPodcasts =
            MainActivity.pref.getBoolean("switch_for_podcasts", true)

        MainActivity.switchForCollections =
            MainActivity.pref.getBoolean("switch_for_collections", true)

        MainActivity.switchForAnnouncements =
            MainActivity.pref.getBoolean("switch_for_announcements", true)

        MainActivity.switchForPostFromSocialNetwork =
            MainActivity.pref.getBoolean("switch_for_post_from_social_network", true)

    }
}



