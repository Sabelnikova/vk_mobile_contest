package com.sabelnikova.vkdiscover.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.sabelnikova.vkdiscover.R
import com.sabelnikova.vkdiscover.di.android.viewmodel.ViewModelFactory
import com.sabelnikova.vkdiscover.model.Attachment
import com.sabelnikova.vkdiscover.model.DiscoverItem
import com.sabelnikova.vkdiscover.model.Photo
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val res = VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
               mainViewModel.saveToken(res.accessToken)
            }

            override fun onError(error: VKError) {
                //todo show error
            }
        })
        //todo if !res show error
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = getViewModel(MainViewModel::class.java)

        mainViewModel.postsLiveData.observe(this, Observer {
            it?.let {

            }
        })

        if (!mainViewModel.userLoggedIn()) {
            VKSdk.login(this, VKScope.WALL)
        }

        setTest()

        skipBtn.setOnClickListener {
            stack.swipe(StackView.SwipeDirection.LEFT)
        }

        likeBtn.setOnClickListener {
            stack.swipe(StackView.SwipeDirection.RIGHT)
        }
    }

    private fun setTest() {
        val adapter = MyAdapter()

        val item = DiscoverItem("post",1, 1, "If you're using a ViewPager together with this layout, you can call setupWithViewPager(ViewPager) to link the two together. ", System.currentTimeMillis(),
                listOf(Attachment("photo", Photo(1, listOf(), "https://www.fresher.ru/wp-content/uploads/2018/03/1.jpg")),
                        Attachment("photo", Photo(1, listOf(), "https://bipbap.ru/wp-content/uploads/2017/12/65620375-6b2b57fa5c7189ba4e3841d592bd5fc1-800-640x426.jpg")),
                        Attachment("photo", Photo(1, listOf(), "https://i.forfun.com/j9pfh92e.jpeg"))))

        val item2 = DiscoverItem("post",1, 1, "Болельщики тульского \"Арсенала\", которые присутствовали на матче с махачкалинским \"Анжи\" в рамках 14-го тура Российской премьер-лиги (РПЛ), рассказали о прощании главного тренера Олега Кононова с фанатами, передает \"Советский спорт\". Эксперт напомнил, что Франция обязана русским солдатам, которые дважды в XX веке спасли страну от неминуемой гибели и потеряли миллионы своих граждан. Царская Россия и Советский Союз спасли свободную Европу во время Первой и Второй мировых войн, пишет автор.\n" +
                "\n" +
                "В дни памяти о событиях Первой мировой войны необходимо забыть про популизм и отдать должное простому народу, каким бы ни было отношение к сегодняшней российской власти, полагает Юссон. По мнению историка, европейскую безопасность не следует строить на основе конфронтации с Москвой.", System.currentTimeMillis(),
                listOf(Attachment("photo", Photo(1, listOf(), "https://img.championat.com/i/82/77/15417882771110466418.jpg")),
                        Attachment("photo", Photo(1, listOf(), "https://img.championat.com/i/82/83/15417882831650560173.jpg"))))

        adapter.setItems(listOf(item, item2))

        adapter.onExpandView = {
            stack.swipeEnabled = false
        }

        adapter.onHideView = {
            stack.swipeEnabled = true
        }

        stack.onStartSwipe = { _, direction ->
            val view: TextView? = when (direction) {
                StackView.SwipeDirection.LEFT -> stack?.getFrontView()?.findViewById(R.id.skipTv)
                StackView.SwipeDirection.RIGHT -> stack?.getFrontView()?.findViewById(R.id.likeTv)
            }
            view?.animate()?.alpha(1f)
        }

        stack.onStopSwipe = { _, direction ->
            val view: TextView? = when (direction) {
                StackView.SwipeDirection.LEFT -> stack?.getFrontView()?.findViewById(R.id.skipTv)
                StackView.SwipeDirection.RIGHT -> stack?.getFrontView()?.findViewById(R.id.likeTv)
            }
            view?.animate()?.alpha(0f)
        }
    }

    private fun <T : ViewModel> getViewModel(aClass: Class<T>): T {
        return ViewModelProviders.of(this, viewModelFactory)
                .get(aClass)
    }
}

