package com.github.qingmei2.sample.ui.main.home

import android.annotation.SuppressLint
import androidx.paging.PagedList
import com.github.qingmei2.mvi.ext.reactivex.flatMapErrorActionObservable
import com.github.qingmei2.sample.entity.ReceivedEvent
import com.github.qingmei2.sample.repository.UserInfoRepository
import com.github.qingmei2.sample.ui.main.common.scrollStateProcessor
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject

@SuppressLint("CheckResult")
class HomeActionProcessorHolder(
    private val repository: HomeRepository,
    private val userRepository: UserInfoRepository
) {
    private val receivedEventsLoadingEventSubject: PublishSubject<HomeResult.LoadingPageResult> =
        PublishSubject.create()

    private val initialActionTransformer =
        ObservableTransformer<HomeAction.InitialAction, HomeResult.InitialResult> { action ->
            action.flatMap<HomeResult.InitialResult> {
                repository.getReceivedPagedList(
                    boundaryCallback = object : PagedList.BoundaryCallback<ReceivedEvent>() {
                        override fun onZeroItemsLoaded() {
                            this@HomeActionProcessorHolder.onZeroItemsLoaded()
                        }

                        override fun onItemAtEndLoaded(itemAtEnd: ReceivedEvent) {
                            this@HomeActionProcessorHolder.onItemAtEndLoaded(itemAtEnd)
                        }
                    }
                ).map(HomeResult::InitialResult).toObservable()
            }
        }

    private val swipeRefreshActionTransformer =
        ObservableTransformer<HomeAction.SwipeRefreshAction, HomeResult.SwipeRefreshResult> { action ->
            action.flatMap {
                repository.swipeRefresh()
                Observable.just(HomeResult.SwipeRefreshResult)
            }
        }

    private fun onZeroItemsLoaded() {
        repository.queryReceivedEventsByPage(userRepository.username, 1)
            .map<HomeResult.LoadingPageResult> { HomeResult.LoadingPageResult.Success(true) }
            .onErrorReturn { HomeResult.LoadingPageResult.Failure(true, it) }
            .startWith(HomeResult.LoadingPageResult.InFlight(true))
            .toObservable()
            .subscribe { receivedEventsLoadingEventSubject.onNext(it) }
    }

    private fun onItemAtEndLoaded(itemAtEnd: ReceivedEvent) {
        val currentPageIndex = (itemAtEnd.indexInResponse / 15) + 1
        val nextPageIndex = currentPageIndex + 1
        repository.queryReceivedEventsByPage(userRepository.username, nextPageIndex)
            .map<HomeResult.LoadingPageResult> { HomeResult.LoadingPageResult.Success(true) }
            .onErrorReturn { HomeResult.LoadingPageResult.Failure(true, it) }
            .toObservable()
            .subscribe { receivedEventsLoadingEventSubject.onNext(it) }
    }

    private val scrollStateChangeTransformer =
        ObservableTransformer<HomeAction.ScrollStateChangedAction, HomeResult> { action ->
            action
                .map { it.state }
                .compose(scrollStateProcessor)
                .map(HomeResult::FloatActionButtonVisibleResult)
        }

    val actionProcessor: ObservableTransformer<HomeAction, HomeResult> =
        ObservableTransformer { actions ->
            actions.publish { shared ->
                Observable.mergeArray(
                    shared.ofType(HomeAction.InitialAction::class.java).compose<HomeResult>(initialActionTransformer),
                    shared.ofType(HomeAction.SwipeRefreshAction::class.java).compose(swipeRefreshActionTransformer),
                    shared.ofType(HomeAction.ScrollToTopAction::class.java).map { HomeResult.ScrollToTopResult },
                    shared.ofType(HomeAction.ScrollStateChangedAction::class.java).compose(scrollStateChangeTransformer),
                    shared.filter { o ->
                        o !is HomeAction.InitialAction
                                && o !is HomeAction.ScrollToTopAction
                                && o !is HomeAction.ScrollStateChangedAction
                    }.flatMapErrorActionObservable()
                ).mergeWith(receivedEventsLoadingEventSubject.hide())
            }
        }
}