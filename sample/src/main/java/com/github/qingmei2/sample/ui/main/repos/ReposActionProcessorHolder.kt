package com.github.qingmei2.sample.ui.main.repos

import android.annotation.SuppressLint
import androidx.paging.PagedList
import com.github.qingmei2.mvi.ext.reactivex.flatMapErrorActionObservable
import com.github.qingmei2.sample.entity.Repo
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.manager.UserManager
import com.github.qingmei2.sample.repository.UserInfoRepository
import com.github.qingmei2.sample.ui.main.common.scrollStateProcessor
import com.github.qingmei2.sample.ui.main.repos.ReposViewModel.Companion.sortByUpdate
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

@SuppressLint("CheckResult")
class ReposActionProcessorHolder(
    private val repository: ReposRepository,
    private val userRepository: UserInfoRepository,
    private val schedulerProvider: SchedulerProvider
) {
    private val repoSortTypeSubject: BehaviorSubject<String> =
        BehaviorSubject.createDefault(sortByUpdate)
    private val reposLoadingEventSubject: PublishSubject<ReposResult> =
        PublishSubject.create()

    private val mViewModelClearEventSubject: PublishSubject<Unit> =
        PublishSubject.create()

    private val initialActionTransformer =
        ObservableTransformer<ReposAction.InitialAction, ReposResult.InitialResult> { action ->
            action.flatMap<ReposResult.InitialResult> {
                repository.getReceivedPagedList(
                    boundaryCallback = object : PagedList.BoundaryCallback<Repo>() {
                        override fun onZeroItemsLoaded() {
                            this@ReposActionProcessorHolder.onZeroItemsLoaded()
                        }

                        override fun onItemAtEndLoaded(itemAtEnd: Repo) {
                            this@ReposActionProcessorHolder.onItemAtEndLoaded(itemAtEnd)
                        }
                    }
                )
                    .map(ReposResult::InitialResult)
                    .toObservable()
            }
        }

    private fun onZeroItemsLoaded() {
        repository.queryReposByPage(UserManager.INSTANCE.login, repoSortTypeSubject.blockingFirst(), 1)
            .toObservable()
            .map<ReposResult.ReposPageResult> { ReposResult.ReposPageResult.Success(true) }
            .onErrorReturn { ReposResult.ReposPageResult.Failure(true, it) }
            .startWith(ReposResult.ReposPageResult.InFlight(true))
            .takeUntil(mViewModelClearEventSubject)
            .subscribe { reposLoadingEventSubject.onNext(it) }
    }

    private fun onItemAtEndLoaded(itemAtEnd: Repo) {
        val currentPageIndex = (itemAtEnd.indexInSortResponse / 15) + 1
        val nextPageIndex = currentPageIndex + 1

        repository.queryReposByPage(UserManager.INSTANCE.login, repoSortTypeSubject.blockingFirst(), nextPageIndex)
            .toObservable()
            .map<ReposResult.ReposPageResult> { ReposResult.ReposPageResult.Success(true) }
            .onErrorReturn { ReposResult.ReposPageResult.Failure(true, it) }
            .takeUntil(mViewModelClearEventSubject)
            .subscribe { reposLoadingEventSubject.onNext(it) }
    }

    private val swipeRefreshActionTransformer =
        ObservableTransformer<ReposAction.SwipeRefreshAction, ReposResult.SwipeRefreshResult> { action ->
            action
                .observeOn(schedulerProvider.io())
                .map {
                    repository.swipeRefresh()
                    ReposResult.SwipeRefreshResult
                }
        }

    private val sortTypeChangedActionTransformer =
        ObservableTransformer<ReposAction.SortTypeChangedAction, ReposResult.SortTypeChangedResult> { action ->
            action
                .observeOn(schedulerProvider.io())
                .map {
                    repoSortTypeSubject.onNext(it.sortType)
                    repository.swipeRefresh()
                    ReposResult.SortTypeChangedResult
                }
        }

    private val scrollStateChangeTransformer =
        ObservableTransformer<ReposAction.ScrollStateChangedAction, ReposResult> { action ->
            action
                .map { it.state }
                .compose(scrollStateProcessor)
                .map(ReposResult::FloatActionButtonVisibleResult)
        }

    val actionProcessor: ObservableTransformer<ReposAction, ReposResult> =
        ObservableTransformer { actions ->
            actions.publish { shared ->
                Observable.mergeArray(
                    shared.ofType(ReposAction.InitialAction::class.java).compose(initialActionTransformer),
                    shared.ofType(ReposAction.SwipeRefreshAction::class.java).compose(swipeRefreshActionTransformer),
                    shared.ofType(ReposAction.SortTypeChangedAction::class.java).compose(
                        sortTypeChangedActionTransformer
                    ),
                    shared.ofType(ReposAction.ScrollToTopAction::class.java).map { ReposResult.ScrollToTopResult },
                    shared.ofType(ReposAction.ScrollStateChangedAction::class.java).compose(scrollStateChangeTransformer),
                    reposLoadingEventSubject,
                    shared.filter { o ->
                        o !is ReposAction.SortTypeChangedAction
                                && o !is ReposAction.ScrollToTopAction
                                && o !is ReposAction.ScrollStateChangedAction
                                && o !is ReposAction.InitialAction
                                && o !is ReposAction.SwipeRefreshAction
                    }.flatMapErrorActionObservable()
                )
            }
        }

    fun onViewModelCleared() {
        if (mViewModelClearEventSubject.hasComplete())
            throw IllegalStateException("can't call onViewModelCleared() repeatedly.")

        mViewModelClearEventSubject.onNext(Unit)
        mViewModelClearEventSubject.onComplete()

        repoSortTypeSubject.onComplete()
        reposLoadingEventSubject.onComplete()
    }
}
