package com.sabelnikova.vkdiscover.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.sabelnikova.vkdiscover.R
import com.sabelnikova.vkdiscover.di.android.viewmodel.ViewModelFactory
import com.sabelnikova.vkdiscover.model.Attachment
import com.sabelnikova.vkdiscover.model.DiscoverItem
import com.sabelnikova.vkdiscover.model.Photo
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MyAdapter()

        val item = DiscoverItem("post", "If you're using a ViewPager together with this layout, you can call setupWithViewPager(ViewPager) to link the two together. ", System.currentTimeMillis(),
                listOf(Attachment("photo", Photo(1, listOf(), "https://www.fresher.ru/wp-content/uploads/2018/03/1.jpg")),
                        Attachment("photo", Photo(1, listOf(), "https://bipbap.ru/wp-content/uploads/2017/12/65620375-6b2b57fa5c7189ba4e3841d592bd5fc1-800-640x426.jpg")),
                        Attachment("photo", Photo(1, listOf(), "https://i.forfun.com/j9pfh92e.jpeg"))))

        val item2 = DiscoverItem("post", "Болельщики тульского \"Арсенала\", которые присутствовали на матче с махачкалинским \"Анжи\" в рамках 14-го тура Российской премьер-лиги (РПЛ), рассказали о прощании главного тренера Олега Кононова с фанатами, передает \"Советский спорт\". Эксперт напомнил, что Франция обязана русским солдатам, которые дважды в XX веке спасли страну от неминуемой гибели и потеряли миллионы своих граждан. Царская Россия и Советский Союз спасли свободную Европу во время Первой и Второй мировых войн, пишет автор.\n" +
                "\n" +
                "В дни памяти о событиях Первой мировой войны необходимо забыть про популизм и отдать должное простому народу, каким бы ни было отношение к сегодняшней российской власти, полагает Юссон. По мнению историка, европейскую безопасность не следует строить на основе конфронтации с Москвой.", System.currentTimeMillis(),
                listOf(Attachment("photo", Photo(1, listOf(), "https://img.championat.com/i/82/77/15417882771110466418.jpg")),
                        Attachment("photo", Photo(1, listOf(), "https://img.championat.com/i/82/83/15417882831650560173.jpg"))))

        adapter.setItems(listOf(item, item2))

        val testAdapter = TestAdapter()
        testAdapter.setItems(listOf("one", "two", "three", "four", "five"))
        stack.adapter = adapter

        adapter.onExpandView = {
            stack.swipeEnabled = false
        }

        adapter.onHideView = {
            stack.swipeEnabled = true
        }

        stack.onStartSwipe = { _, direction ->
            val view: TextView? = when (direction){
                StackView.SwipeDirection.LEFT -> stack?.getFrontView()?.findViewById(R.id.skipTv)
                StackView.SwipeDirection.RIGHT -> stack?.getFrontView()?.findViewById(R.id.likeTv)
            }
            view?.animate()?.alpha(1f)
        }

        stack.onStopSwipe = { _, direction ->
            val view: TextView? = when (direction){
                StackView.SwipeDirection.LEFT -> stack?.getFrontView()?.findViewById(R.id.skipTv)
                StackView.SwipeDirection.RIGHT -> stack?.getFrontView()?.findViewById(R.id.likeTv)
            }
            view?.animate()?.alpha(0f)
        }

        skipBtn.setOnClickListener {
            stack.swipe(StackView.SwipeDirection.LEFT)
        }

        likeBtn.setOnClickListener {
            stack.swipe(StackView.SwipeDirection.RIGHT)
        }
    }

    private fun <T : ViewModel> getViewModel(aClass: Class<T>): T {
        return ViewModelProviders.of(this, viewModelFactory)
                .get(aClass)
    }
}

class TestAdapter: StackView.Adapter<String>(){
    override fun getView(parent: ViewGroup, position: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.test, parent, false)
        val userNameTv = view.findViewById<TextView>(R.id.textView2)
        userNameTv.text = getItem(position)

        return view
    }

}

class MyAdapter: StackView.Adapter<DiscoverItem>(){

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var onExpandView: (() -> Unit)? = null
    var onHideView: (() -> Unit)? = null

    override fun getView(parent: ViewGroup, position: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        val tv = view.findViewById<TextView>(R.id.textTv)
        val viewPager = view.findViewById<ViewPager>(R.id.photoViewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.photoTabLayout)
        val userNameTv = view.findViewById<TextView>(R.id.userNameTv)
        val dateTv = view.findViewById<TextView>(R.id.dateTv)
        val avatarIv = view.findViewById<ImageView>(R.id.avatarIv)
        val scrollView = view.findViewById<LockableScrollView>(R.id.scrollView)
        val expandView = view.findViewById<View>(R.id.expandTv)
        val expandTv = view.findViewById<TextView>(R.id.expandTv)
        val expandIv = view.findViewById<View>(R.id.expandIv)
        val container = view.findViewById<ViewGroup>(R.id.container)

        var scrollViewPrepared = false

        var expanded = false

        scrollView.scrollable = false

        view.viewTreeObserver.addOnGlobalLayoutListener {
            if (!scrollViewPrepared) {
                val childHeight = container.height
                val isScrollable = scrollView.height < childHeight

                if (isScrollable) {
                    tv.maxLines = 3
                    expandView.visibility = View.VISIBLE
                }
                scrollViewPrepared = true
            }
        }

        expandView.setOnClickListener {
            if (expanded){
                scrollView.smoothScrollTo(0, 0)
                onHideView?.invoke()
                val newBottomMargin = 200
                val a = object :Animation(){
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        val params = scrollView.layoutParams as FrameLayout.LayoutParams
                        params.bottomMargin = (newBottomMargin * interpolatedTime).toInt()
                        scrollView.layoutParams = params
                        if (interpolatedTime == 1f){
                            tv.maxLines = 3
                            container.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                a.duration = 1000
                scrollView.startAnimation(a)

                scrollView.scrollable = false
                expandIv?.rotation = 0f
                expandTv?.setText(R.string.expand)

            } else {
                tv.maxLines = Int.MAX_VALUE
                onExpandView?.invoke()
                val newBottomMargin = 0
                val a = object :Animation(){
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        val params = scrollView.layoutParams as FrameLayout.LayoutParams
                        params.bottomMargin = (newBottomMargin * interpolatedTime).toInt()
                        scrollView.layoutParams = params
                    }
                }
                a.duration = 1000
                scrollView.startAnimation(a)
                container.setPadding(0, 0, 0, 200)

                scrollView.scrollable = true
                expandIv?.rotation = 180f
                expandTv?.setText(R.string.hide)
            }
            expanded = !expanded
        }

        tv.text = getItem(position).text

        userNameTv.text = "Иван Иванов"
        dateTv.text = dateFormat.format(Date(getItem(position).date))
        avatarIv.setImageResource(R.drawable.ic_like_36)

        val adapter = PhotoViewPagerAdapter()
        adapter.setData(getItem(position).attachments.map { it.photo.accessKey })
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        return view
    }
}