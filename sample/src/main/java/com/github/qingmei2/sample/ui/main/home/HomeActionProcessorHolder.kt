package com.github.qingmei2.sample.ui.main.home

import arrow.core.left
import com.github.qingmei2.mvi.ext.paging.IntPageKeyedData
import com.github.qingmei2.mvi.ext.paging.IntPageKeyedDataSource
import com.github.qingmei2.mvi.ext.paging.Paging
import com.github.qingmei2.mvi.ext.reactivex.flatMapErrorActionObservable
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.ReceivedEvent
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.manager.UserManager
import com.github.qingmei2.sample.ui.main.common.scrollStateProcessor
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class HomeActionProcessorHolder(
    private val repository: HomeRepository,
    private val schedulerProvider: SchedulerProvider
) {
    private val initialActionTransformer =
        ObservableTransformer<HomeAction.InitialAction, HomeResult.InitialResult> { action ->
            action.flatMap<HomeResult.InitialResult> {
                Paging.buildPageKeyedDataSource(receivedEventDataSource)
                    .map(HomeResult.InitialResult::Success)
                    .toObservable()
            }
        }

    private val scrollStateChangeTransformer =
        ObservableTransformer<HomeAction.ScrollStateChangedAction, HomeResult> { action ->
            action
                .map { it.state }
                .compose(scrollStateProcessor)
                .map(HomeResult::FloatActionButtonVisibleResult)
        }

    private val receivedEventDataSource: IntPageKeyedDataSource<ReceivedEvent>
        get() = IntPageKeyedDataSource(
            loadInitial = {
                repository
                    .queryReceivedEvents(UserManager.INSTANCE.login, pageIndex = 1, perPage = 15)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .onErrorReturn { Errors.ErrorWrapper(it).left() }
                    .flatMap { either ->
                        either.fold({
                            Flowable.empty<IntPageKeyedData<ReceivedEvent>>()
                        }, { datas ->
                            Flowable.just(
                                IntPageKeyedData.build(
                                    data = datas,
                                    pageIndex = 1,
                                    hasAdjacentPageKey = datas.isNotEmpty()
                                )
                            )
                        })
                    }
            },
            loadAfter = { param ->
                repository
                    .queryReceivedEvents(UserManager.INSTANCE.login, param.key, perPage = 15)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .onErrorReturn { Errors.ErrorWrapper(it).left() }
                    .flatMap { either ->
                        either.fold({
                            Flowable.empty<IntPageKeyedData<ReceivedEvent>>()
                        }, { datas ->
                            Flowable.just(
                                IntPageKeyedData.build(
                                    data = datas,
                                    pageIndex = param.key,
                                    hasAdjacentPageKey = datas.isNotEmpty()
                                )
                            )
                        })
                    }
            }
        )

    val actionProcessor: ObservableTransformer<HomeAction, HomeResult> =
        ObservableTransformer { actions ->
            actions.publish { shared ->
                Observable.merge(
                    shared.ofType(HomeAction.InitialAction::class.java).compose<HomeResult>(initialActionTransformer),
                    shared.ofType(HomeAction.ScrollToTopAction::class.java).map { HomeResult.ScrollToTopResult },
                    shared.ofType(HomeAction.ScrollStateChangedAction::class.java).compose(scrollStateChangeTransformer),
                    shared.filter { o ->
                        o !is HomeAction.InitialAction
                                && o !is HomeAction.ScrollToTopAction
                                && o !is HomeAction.ScrollStateChangedAction
                    }.flatMapErrorActionObservable()
                )
            }
        }
}