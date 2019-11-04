package com.github.qingmei2.sample.ui.main.repos

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.paging.PagedList
import com.github.qingmei2.mvi.base.view.fragment.BaseFragment
import com.github.qingmei2.mvi.ext.reactivex.throttleFirstClicks
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.Repo
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.utils.jumpBrowser
import com.github.qingmei2.sample.utils.toast
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_repos.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReposFragment : BaseFragment<ReposIntent, ReposViewState>() {

    private val mRefreshSubject: PublishSubject<ReposIntent.RefreshIntent> =
        PublishSubject.create()
    private val mSortTypePublishSubject: PublishSubject<ReposIntent.SortTypeChangeIntent> =
        PublishSubject.create()
    private val mScrollToTopSubject: PublishSubject<ReposIntent.ScrollToTopIntent> =
        PublishSubject.create()
    private val mScrollStateChangedSubject: PublishSubject<ReposIntent.ScrollStateChangedIntent> =
        PublishSubject.create()

    @Inject
    lateinit var mViewModel: ReposViewModel
    @Inject
    lateinit var mSchedulerProvider: SchedulerProvider

    override val layoutId: Int = R.layout.fragment_repos

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mToolbar.inflateMenu(R.menu.menu_repos_filter_type)

        binds()
    }

    private fun binds() {
        mFabButton.throttleFirstClicks()
            .map { ReposIntent.ScrollToTopIntent }
            .autoDisposable(scopeProvider)
            .subscribe(mScrollToTopSubject)
        mRecyclerView.scrollStateChanges()
            .map(ReposIntent::ScrollStateChangedIntent)
            .autoDisposable(scopeProvider)
            .subscribe(mScrollStateChangedSubject)
        mSwipeRefreshLayout.refreshes()
            .map { ReposIntent.RefreshIntent }
            .autoDisposable(scopeProvider)
            .subscribe(mRefreshSubject)
        mToolbar.itemClicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .map {
                when (it.itemId) {
                    R.id.menu_repos_letter -> ReposViewModel.sortByLetter
                    R.id.menu_repos_update -> ReposViewModel.sortByUpdate
                    R.id.menu_repos_created -> ReposViewModel.sortByCreated
                    else -> throw IllegalArgumentException("error menuItem id.")
                }
            }
            .map(ReposIntent::SortTypeChangeIntent)
            .autoDisposable(scopeProvider)
            .subscribe(mSortTypePublishSubject)

        mViewModel.states()
            .observeOn(mSchedulerProvider.ui())
            .autoDisposable(scopeProvider)
            .subscribe(this::render)

        mViewModel.processIntents(intents())
    }

    override fun intents(): Observable<ReposIntent> {
        return Observable.mergeArray(
            initialIntent(),
            mSortTypePublishSubject.distinctUntilChanged(),
            mScrollToTopSubject,
            mScrollStateChangedSubject,
            mRefreshSubject
        )
    }

    private fun initialIntent(): Observable<ReposIntent> {
        return Observable.just(ReposIntent.InitialIntent)
    }

    override fun render(state: ReposViewState) {
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
                is ReposUIEvent.InitialSuccess -> {
                    initPagedListAdapter(this.pageList)
                }
                is ReposUIEvent.ScrollToTopEvent -> {
                    mRecyclerView.scrollToPosition(0)
                }
                is ReposUIEvent.FloatActionButtonEvent -> {
                    switchFabState(this.visible)
                }
            }
        }
    }

    private fun initPagedListAdapter(pageList: PagedList<Repo>) {
        val mAdapter = mRecyclerView.adapter as ReposPagedListAdapter?
        when (mAdapter == null) {
            true -> {
                val adapter = ReposPagedListAdapter()
                adapter.observeEvent()
                    .doOnNext { event ->
                        when (event) {
                            is RepoPagedListItemEvent.ClickEvent -> {
                                context?.jumpBrowser(event.url)
                            }
                        }
                    }
                    .autoDisposable(scopeProvider)
                    .subscribe()

                mRecyclerView.adapter = adapter
                adapter.submitList(pageList)
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