package com.github.qingmei2.sample.ui.main.repos

import com.github.qingmei2.sample.http.scheduler.SchedulerProvider

class ReposActionProcessorHolder(
    private val repository: ReposDataSource,
    private val schedulerProvider: SchedulerProvider
) {
    /*private val initialActionTransformer =
            ObservableTransformer<HomeAction.InitialAction, HomeResult.InitialResult> { action ->
                action.flatMap<HomeResult.InitialResult> {
                    Paging.buildPageKeyedDataSource(receivedEventDataSource)
                            .map(HomeResult.InitialResult::Success)
                            .toObservable()
                }
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
                            shared.filter { o ->
                                o !is HomeAction.InitialAction
                            }.flatMapErrorActionObservable()
                    )
                }
            }*/
}