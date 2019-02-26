package com.github.qingmei2.sample.ui.main.home

import android.animation.ObjectAnimator
import androidx.paging.PagedList
import com.github.qingmei2.mvi.base.view.fragment.BaseFragment
import com.github.qingmei2.mvi.ext.reactivex.throttleFirstClicks
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.base.BaseApplication
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.ReceivedEvent
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.ui.main.common.scrollStateProcessor
import com.github.qingmei2.sample.utils.jumpBrowser
import com.github.qingmei2.sample.utils.toast
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class HomeFragment : BaseFragment<HomeIntent, HomeViewState>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(homeKodeinModule)
    }

    override val layoutId: Int = R.layout.fragment_home

    private val mViewModel: HomeViewModel by instance()
    private val mSchedulerProvider: SchedulerProvider by instance()

    override fun onStart() {
        super.onStart()

        binds()
    }

    private fun binds() {
        mViewModel.states()
            .autoDisposable(scopeProvider)
            .subscribe(this::render)
        mFabButton.throttleFirstClicks()
            .map { 0 }
            .autoDisposable(scopeProvider)
            .subscribe(mRecyclerView::scrollToPosition)
        mRecyclerView.scrollStateChanges()
            .compose(scrollStateProcessor)
            .observeOn(mSchedulerProvider.ui())
            .autoDisposable(scopeProvider)
            .subscribe { switchFabState(it) }

        mViewModel.processIntents(intents())
    }

    override fun intents(): Observable<HomeIntent> {
        return initialIntent()
//        return Observable.merge(
//                initialIntent(),
//                initialIntent()
//        )
    }

    private fun initialIntent(): Observable<HomeIntent> = Observable.just(HomeIntent.InitialIntent)

    override fun render(state: HomeViewState) {
        state.error?.apply {
            when (this) {
                is Errors.SimpleMessageError -> toast(simpleMessage)
                is Errors.ErrorWrapper -> {
                    toast("error:${errors.localizedMessage}")
                }
                else -> {
                    toast("error:$localizedMessage")
                }
            }
        }

        mSwipeRefreshLayout.isRefreshing = state.isRefreshing

        state.uiEvent.apply {
            when (this) {
                is HomeViewState.HomeUIEvent.InitialSuccess -> {
                    initPagedListAdapter(this.pageList)
                }
            }
        }
    }

    private fun initPagedListAdapter(pageList: PagedList<ReceivedEvent>) {
        val mAdapter = HomePagedListAdapter()
        mRecyclerView.adapter = mAdapter
        mAdapter.submitList(pageList)
        mAdapter.observeEvent()
            .doOnNext { event ->
                when (event) {
                    is HomePagedListItemEvent.ClickEvent -> {
                        BaseApplication.INSTANCE.jumpBrowser(event.url)
                    }
                }
            }
            .autoDisposable(scopeProvider)
            .subscribe()
    }

    private fun switchFabState(show: Boolean) =
        when (show) {
            false -> ObjectAnimator.ofFloat(mFabButton, "translationX", 250f, 0f)
            true -> ObjectAnimator.ofFloat(mFabButton, "translationX", 0f, 250f)
        }.apply {
            duration = 300
            start()
        }
}