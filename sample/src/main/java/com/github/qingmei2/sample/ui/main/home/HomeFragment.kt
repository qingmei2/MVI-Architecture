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
import com.github.qingmei2.sample.utils.jumpBrowser
import com.github.qingmei2.sample.utils.toast
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class HomeFragment : BaseFragment<HomeIntent, HomeViewState>() {

    private val mScrollToTopSubject: PublishSubject<HomeIntent.ScrollToTopIntent> =
        PublishSubject.create()
    private val mScrollStateChangedSubject: PublishSubject<HomeIntent.ScrollStateChangedIntent> =
        PublishSubject.create()
    private val mRefreshSubject: PublishSubject<HomeIntent.RefreshIntent> =
        PublishSubject.create()

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(homeKodeinModule)
    }

    override val layoutId: Int = R.layout.fragment_home

    private val mViewModel: HomeViewModel by instance()

    private val mSchedulerProvider: SchedulerProvider by instance()

    private val mAdapter: HomePagedListAdapter = HomePagedListAdapter()

    override fun onStart() {
        super.onStart()

        binds()
    }

    private fun binds() {
        mViewModel.states()
            .observeOn(mSchedulerProvider.ui())
            .autoDisposable(scopeProvider)
            .subscribe(this::render)

        mFabButton.throttleFirstClicks()
            .map { HomeIntent.ScrollToTopIntent }
            .autoDisposable(scopeProvider)
            .subscribe(mScrollToTopSubject)
        mRecyclerView.scrollStateChanges()
            .map(HomeIntent::ScrollStateChangedIntent)
            .autoDisposable(scopeProvider)
            .subscribe(mScrollStateChangedSubject)
        mSwipeRefreshLayout.refreshes()
            .map { HomeIntent.RefreshIntent }
            .autoDisposable(scopeProvider)
            .subscribe(mRefreshSubject)

        mViewModel.processIntents(intents())
    }

    override fun intents(): Observable<HomeIntent> {
        return Observable.merge(
            initialIntent(),
            mScrollToTopSubject,
            mScrollStateChangedSubject,
            mRefreshSubject
        )
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
                is HomeUIEvent.InitialSuccess -> {
                    initPagedListAdapter(this.pageList)
                }
                is HomeUIEvent.ScrollToTopEvent -> {
                    mRecyclerView.scrollToPosition(0)
                }
                is HomeUIEvent.FloatActionButtonEvent -> {
                    switchFabState(this.visible)
                }
            }
        }
    }

    private fun initPagedListAdapter(pageList: PagedList<ReceivedEvent>) {
        when (mRecyclerView.adapter == null) {
            true -> {
                mRecyclerView.adapter = mAdapter
                mAdapter.submitList(pageList)
                mAdapter.observeEvent()
                    .doOnNext { event ->
                        when (event) {
                            is HomePagedListItemEvent.ClickEvent -> {
                                context?.jumpBrowser(event.url)
                            }
                        }
                    }
                    .autoDisposable(scopeProvider)
                    .subscribe()
            }
            false -> {
                mAdapter.submitList(pageList)
            }
        }
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