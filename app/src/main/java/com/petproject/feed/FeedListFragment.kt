package com.petproject.feed


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.error_with_internet_connection_item.*
import kotlinx.android.synthetic.main.error_with_server_item.*
import kotlinx.android.synthetic.main.fragment_feed_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FeedListFragment : Fragment() {

    private var feedViewModel: FeedViewModel? = null
    private var feedListAdapter: FeedListAdapter? = null
    private var errorViewWithServer: View? = null
    private var errorViewWithInternet: View? = null
    private val mRetrofitClient: RetrofitClientInstance = RetrofitClientInstance.instance!!
    private var showBtn = false
    private var isExpanded = true

    companion object {

        private lateinit var feedListFragment: FeedListFragment
        fun getInstance(): FeedListFragment {
            return feedListFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed_list, container, false)
    }

    override fun onResume() {
        super.onResume()

        feedListAdapter?.notifyDataSetChanged()

        if (showBtn)
            getLastUpdate()
        else
            showBtn = true

        val urlPhoto = pref.getString("feedListUserAvatar", "empty")!!

        if (urlPhoto != "empty") {
            GlideApp.with(this).load(urlPhoto)
                .apply(RequestOptions.circleCropTransform())
                .into(toolbar.toolbar_user_avatar)
        } else {
            toolbar.toolbar_user_avatar.setImageResource(R.drawable.ic_default_user_avatar)
        }
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedProgressBar.visibility = View.VISIBLE
        feedSwipeToRefresh.visibility = View.GONE

        val v = layoutInflater.inflate(R.layout.custom_toolbar, null)

        feedListFragment = this

        feedViewModel = ViewModelProvider(this).get(FeedViewModel::class.java)

        v.toolbar_title.text = getString(R.string.feed_title)

        toolbar.addView(v)

        if (pref.getString("vkProfileImage", "")!!.isNotEmpty()) {

            GlideApp.with(this).load(pref.getString("vkProfileImage", ""))
                .apply(RequestOptions.circleCropTransform())
                .into(toolbar.toolbar_user_avatar)

        }

        if (pref.getString("googleProfileImage", "")!!.isNotEmpty()) {

            GlideApp.with(this).load(pref.getString("googleProfileImage", ""))
                .apply(RequestOptions.circleCropTransform())
                .into(toolbar.toolbar_user_avatar)

        }

        v.toolbar_user_avatar.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivityForResult(intent, (activity as MainActivity).PROFILE_REQUEST)
        }

        new_posts_btn.setOnClickListener {
            feedViewModel?.refresh()
            scrollUp()
            new_posts_btn.visibility = View.GONE
        }

        (toolbar.parent as AppBarLayout).addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset != 0) {
                val layoutParams = new_posts_btn.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(
                    0,
                    dpToPx(20F, resources),
                    0,
                    0
                )
                new_posts_btn.layoutParams = layoutParams
            } else {
                val layoutParams = new_posts_btn.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(
                    0,
                    dpToPx(40F, resources),
                    0,
                    0
                )
                new_posts_btn.layoutParams = layoutParams
            }
        })




        feedSwipeToRefresh.setOnRefreshListener {
            feedViewModel?.refresh()
            scrollUp()
            new_posts_btn.visibility = View.GONE
        }

        feedSwipeToRefresh.setColorSchemeResources(R.color.colorAccent)

        initAdapter()
        initState()

        buildErrorWithServerView()
        buildErrorWithInternetConnectionView()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            //do when hidden
        } else {
            getLastUpdate()
        }
    }

    private fun initAdapter() {

        feedListAdapter = FeedListAdapter(feedListFragment, feedRecycleView)
        feedRecycleView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        feedRecycleView.adapter = feedListAdapter
        feedViewModel?.feedList?.observe(
            viewLifecycleOwner,
            Observer {
                feedSwipeToRefresh.isRefreshing = false

                if (it.isNotEmpty())
                    feedListAdapter?.submitList(it)
            }
        )
    }

    private fun initState() {
        feedViewModel?.getState()?.observe(
            viewLifecycleOwner,
            Observer { state ->

                when (state!!) {
                    State.DONE -> {
                        feedProgressBar.visibility = View.INVISIBLE
                        feedSwipeToRefresh.visibility = View.VISIBLE
                        feedRecycleView.visibility = View.VISIBLE
                        errorViewWithServer?.visibility = View.GONE
                        errorViewWithInternet?.visibility = View.GONE
                        error_with_server_layout.visibility = View.GONE
                        error_with_internet_layout.visibility = View.GONE
                    }

                    State.LOADING -> {
                        feedSwipeToRefresh.visibility = View.VISIBLE
                    }

                    State.ERROR -> {

                        feedRecycleView.visibility = View.GONE

                        if (isNetworkAvailable(requireContext())) {
                            error_with_server_layout.visibility = View.VISIBLE
                            error_with_internet_layout?.visibility = View.GONE
                        } else {
                            error_with_server_layout.visibility = View.GONE
                            error_with_internet_layout?.visibility = View.VISIBLE
                        }

                        feedProgressBar.visibility = View.INVISIBLE
                    }
                }

                if (feedViewModel != null && !feedViewModel!!.listIsEmpty())
                    feedListAdapter?.setState(state)
            })
    }

    fun scrollUp() {
        if (feedListAdapter != null)
            feedRecycleView.scrollToPosition(0)

        if (toolbar.parent is AppBarLayout)
            (toolbar.parent as AppBarLayout).setExpanded(true, true)
    }

    private fun buildErrorWithServerView() {
        error_item_btn_retry.setOnClickListener {
            feedViewModel?.refresh()
            feedSwipeToRefresh.isRefreshing = true
        }

        error_item_btn_open_bookmark.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }


    private fun buildErrorWithInternetConnectionView() {
        error_with_internet_connection_item_btn_retry.setOnClickListener {
            feedViewModel?.refresh()
            feedSwipeToRefresh.isRefreshing = true
        }

        error_with_internet_connection_item_btn_open_bookmark.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getLastUpdate() {
        mRetrofitClient.feedEndpoint!!.getLastUpdate(
            accessToken = pref.getString("accessToken", "")!!
        )
            .enqueue(object : Callback<FeedListLastUpdateDto> {
                override fun onFailure(call: Call<FeedListLastUpdateDto>, t: Throwable) {
                    Log.w(this::class.java.name, t.localizedMessage!!)
                }

                override fun onResponse(
                    call: Call<FeedListLastUpdateDto>,
                    response: Response<FeedListLastUpdateDto>
                ) {

                    if (response.isSuccessful) {

                        if (response.body()!!.lastUpdate != pref.getLong("lastUpdate", 0)) {

                            pref.edit()
                                .putLong("lastUpdate", response.body()!!.lastUpdate)
                                .apply()

                            if (pref.getLong("lastUpdate", 0) != 0L) {
                                new_posts_btn.visibility = View.VISIBLE
                            }

                        }

                    }
                }
            })
    }
}
