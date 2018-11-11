package com.sabelnikova.vkdiscover.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.sabelnikova.vkdiscover.R
import com.sabelnikova.vkdiscover.di.android.viewmodel.ViewModelFactory
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var mainViewModel: MainViewModel

    private val discoverItemsAdapter = DiscoverItemsAdapter()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val res = VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                mainViewModel.saveToken(res.accessToken)
            }

            override fun onError(error: VKError) {
                showError(error.errorMessage)
            }
        })
        if (!res) showError(getString(R.string.vk_auth_error))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainViewModel::class.java)
        if (!mainViewModel.userLoggedIn()) {
            VKSdk.login(this, VKScope.WALL)
        }

        mainViewModel.postsLiveData.observe(this, Observer {
            it?.let { response ->
                if (response.isSuccessful) {
                    it.body?.items?.let { items -> discoverItemsAdapter.addItems(items) }
                } else {
                    response.error?.message?.let { message -> showError(message) }
                }
                progressBar.visibility = View.GONE
            }
        })
        mainViewModel.loadNext()

        setupStackView()

        skipBtn.setOnClickListener {
            stack.swipe(StackView.SwipeDirection.LEFT)
        }

        likeBtn.setOnClickListener {
            stack.swipe(StackView.SwipeDirection.RIGHT)
        }
    }

    private fun setupStackView() {
        stack.adapter = discoverItemsAdapter

        discoverItemsAdapter.onExpandView = {
            stack.swipeEnabled = false
        }

        discoverItemsAdapter.onHideView = {
            stack.swipeEnabled = true
        }

        stack.onSwipeCompleted = { position, direction ->
            when (direction) {
                StackView.SwipeDirection.LEFT -> {
                    ignorePost(position)
                }
                StackView.SwipeDirection.RIGHT -> {
                    likePost(position)
                }
            }
        }

        stack.onSwipeProgress = { progress, direction ->
            val appearingView: View?
            val disappearingView: View?
            when (direction) {
                StackView.SwipeDirection.LEFT -> {
                    appearingView = (stack?.frontViewHolder as? DiscoverItemsAdapter.DiscoverItemViewHolder)?.skipTv
                    disappearingView = (stack?.frontViewHolder as? DiscoverItemsAdapter.DiscoverItemViewHolder)?.likeTv
                }
                StackView.SwipeDirection.RIGHT -> {
                    appearingView = (stack?.frontViewHolder as? DiscoverItemsAdapter.DiscoverItemViewHolder)?.likeTv
                    disappearingView = (stack?.frontViewHolder as? DiscoverItemsAdapter.DiscoverItemViewHolder)?.skipTv
                }
            }
            appearingView?.alpha = progress * 2
            disappearingView?.apply {
                if (progress <= 0.1) {
                    alpha = 0f
                } else {
                    if (alpha != 0f) {
                        alpha = progress / 2
                    }
                }
            }
        }

        stack.onStopSwipe = { _, direction ->
            val view: View? = when (direction) {
                StackView.SwipeDirection.LEFT -> (stack?.frontViewHolder as? DiscoverItemsAdapter.DiscoverItemViewHolder)?.skipTv
                StackView.SwipeDirection.RIGHT -> (stack?.frontViewHolder as? DiscoverItemsAdapter.DiscoverItemViewHolder)?.likeTv
            }
            view?.animate()?.alpha(0f)
        }

        stack.onItemAppearInBack = {
            if (discoverItemsAdapter.getItemsCount() - it < 3) {
                mainViewModel.loadNext()
            }
        }
    }

    private fun likePost(position: Int) {
        mainViewModel.like(discoverItemsAdapter.getItem(position)).observe(this, Observer {
            if (it?.isSuccessful != true) {
                it?.error?.message?.let { message -> showError(message) }
            }
        })
    }

    private fun ignorePost(position: Int) {
        mainViewModel.ignore(discoverItemsAdapter.getItem(position)).observe(this, Observer {
            if (it?.isSuccessful != true) {
                it?.error?.message?.let { message -> showError(message) }
            }
        })
    }

    private fun showError(message: String) {
        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
    }
}

