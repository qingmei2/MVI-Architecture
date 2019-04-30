package com.github.qingmei2.sample.ui.main.home

import androidx.paging.PagedList
import com.github.qingmei2.mvi.base.repository.BaseRepositoryBoth
import com.github.qingmei2.mvi.base.repository.ILocalDataSource
import com.github.qingmei2.mvi.base.repository.IRemoteDataSource
import com.github.qingmei2.mvi.ext.paging.toRxPagedList
import com.github.qingmei2.sample.db.UserDatabase
import com.github.qingmei2.sample.entity.ReceivedEvent
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import io.reactivex.Completable
import io.reactivex.Flowable

class HomeRepository(
    remoteDataSource: HomeRemoteDataSource,
    localDataSource: HomeLocalDataSource
) : BaseRepositoryBoth<HomeRemoteDataSource, HomeLocalDataSource>(remoteDataSource, localDataSource) {

    fun swipeRefresh() {
        localDataSource.clearOldData()
    }

    fun getReceivedPagedList(
        boundaryCallback: PagedList.BoundaryCallback<ReceivedEvent>
    ): Flowable<PagedList<ReceivedEvent>> {
        return localDataSource.fetchPagedListFromDb(boundaryCallback)
    }

    fun queryReceivedEventsByPage(
        username: String,
        pageIndex: Int,
        perPage: Int = 15
    ): Flowable<List<ReceivedEvent>> {
        return remoteDataSource.fetchEventsByPage(username, pageIndex, perPage)
            .flatMap {
                localDataSource.insertNewPagedEventData(it)
                    .andThen(Flowable.just(it))
            }
    }
}

class HomeRemoteDataSource(
    private val serviceManager: ServiceManager,
    private val schedulers: SchedulerProvider
) : IRemoteDataSource {

    fun fetchEventsByPage(
        username: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<List<ReceivedEvent>> {
        return fetchEventsByPageInternal(username, pageIndex, perPage)
    }

    private fun fetchEventsByPageInternal(
        username: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<List<ReceivedEvent>> {
        return serviceManager.userService
            .queryReceivedEvents(username, pageIndex, perPage)
            .subscribeOn(schedulers.io())
    }
}


class HomeLocalDataSource(
    private val db: UserDatabase,
    private val schedulers: SchedulerProvider
) : ILocalDataSource {

    fun fetchPagedListFromDb(
        boundaryCallback: PagedList.BoundaryCallback<ReceivedEvent>
    ): Flowable<PagedList<ReceivedEvent>> {
        return db.userReceivedEventDao().queryEvents()
            .toRxPagedList(
                boundaryCallback = boundaryCallback,
                fetchSchedulers = schedulers.io()
            )
    }

    fun clearOldData() {
        db.runInTransaction {
            db.userReceivedEventDao().clearReceivedEvents()
        }
    }

    fun insertNewPagedEventData(newPage: List<ReceivedEvent>): Completable {
        return Completable
            .fromAction { insertDataInternal(newPage) }
    }

    private fun insertDataInternal(newPage: List<ReceivedEvent>) {
        db.runInTransaction {
            val start = db.userReceivedEventDao().getNextIndexInReceivedEvents()
            val items = newPage.mapIndexed { index, child ->
                child.indexInResponse = start + index
                child
            }
            db.userReceivedEventDao().insert(items)
        }
    }
}