package com.sabelnikova.vkdiscover.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        val adapter = MyAdapter()
        adapter.setItems(listOf("one", "two", "three", "four"))
        stack.adapter = adapter
    }

    private fun <T : ViewModel> getViewModel(aClass: Class<T>): T {
        return ViewModelProviders.of(this, viewModelFactory)
                .get(aClass)
    }
}

class MyAdapter: StackView.Adapter<String>(){
    override fun getView(parent: ViewGroup, position: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        val tv = view.findViewById<TextView>(R.id.userNameTv)
        tv.text = getItem(position)
        return view
    }
}