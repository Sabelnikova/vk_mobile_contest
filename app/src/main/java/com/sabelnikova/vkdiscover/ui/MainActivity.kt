package com.sabelnikova.vkdiscover.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.sabelnikova.vkdiscover.R
import com.sabelnikova.vkdiscover.di.android.viewmodel.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainViewModel = getViewModel(MainViewModel::class.java)
        mainViewModel.getDiscover().observe(this, Observer {
            text.text = it?.body?.items?.get(0)?.text
        })
    }

    private fun <T : ViewModel> getViewModel(aClass: Class<T>): T {
        return ViewModelProviders.of(this, viewModelFactory)
                .get(aClass)
    }
}
