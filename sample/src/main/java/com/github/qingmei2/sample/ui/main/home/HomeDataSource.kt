package com.github.qingmei2.sample.ui.main.home

import arrow.core.Either
import com.github.qingmei2.mvi.base.repository.BaseRepositoryRemote
import com.github.qingmei2.mvi.base.repository.IRemoteDataSource
import com.github.qingmei2.sample.entity.DISPLAY_EVENT_TYPES
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.ReceivedEvent
import com.github.qingmei2.sample.http.service.ServiceManager
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer

class HomeRepository(
    remoteDataSource: HomeRemoteDataSource
) : BaseRepositoryRemote<HomeRemoteDataSource>(remoteDataSource) {

    fun queryReceivedEvents(
        username: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<Either<Errors, List<ReceivedEvent>>> =
        remoteDataSource.queryReceivedEvents(username, pageIndex, perPage)

}

class HomeRemoteDataSource(private val serviceManager: ServiceManager) : IRemoteDataSource {

    private fun filterEvents(): FlowableTransformer<List<ReceivedEvent>, List<ReceivedEvent>> =
        FlowableTransformer { datas ->
            datas.flatMap { Flowable.fromIterable(it) }
                .filter { DISPLAY_EVENT_TYPES.contains(it.type) }
                .toList()
                .toFlowable()
        }

    fun queryReceivedEvents(
        username: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<Either<Errors, List<ReceivedEvent>>> =
        serviceManager.userService
            .queryReceivedEvents(username, pageIndex, perPage)
            .compose(filterEvents())        // except the MemberEvent
            .map { list ->
                when (list.isEmpty()) {
                    true -> Either.left(Errors.EmptyResultsError)
                    false -> Either.right(list)
                }
            }
}