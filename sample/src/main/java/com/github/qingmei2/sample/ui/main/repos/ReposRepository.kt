package com.github.qingmei2.sample.ui.main.repos

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.paging.PagedList
import com.github.qingmei2.mvi.base.repository.BaseRepositoryBoth
import com.github.qingmei2.mvi.base.repository.ILocalDataSource
import com.github.qingmei2.mvi.base.repository.IRemoteDataSource
import com.github.qingmei2.mvi.ext.paging.toRxPagedList
import com.github.qingmei2.sample.db.UserDatabase
import com.github.qingmei2.sample.entity.Repo
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import io.reactivex.Completable
import io.reactivex.Flowable

class ReposRepository(
    remote: RemoteReposDataSource,
    local: LocalReposDataSource
) : BaseRepositoryBoth<RemoteReposDataSource, LocalReposDataSource>(remote, local) {

    @WorkerThread
    fun swipeRefresh() {
        localDataSource.clearOldData()
    }

    @MainThread
    fun getReceivedPagedList(
        boundaryCallback: PagedList.BoundaryCallback<Repo>
    ): Flowable<PagedList<Repo>> {
        return localDataSource.fetchPagedListFromDb(boundaryCallback)
    }

    @MainThread
    fun queryReposByPage(
        username: String,
        sortType: String,
        pageIndex: Int,
        perPage: Int = 15
    ): Flowable<List<Repo>> {
        return remoteDataSource.fetchReposByPage(username, sortType, pageIndex, perPage)
            .flatMap {
                localDataSource.insertNewPagedEventData(it)
                    .andThen(Flowable.just(it))
            }
    }
}

class RemoteReposDataSource(
    private val serviceManager: ServiceManager,
    private val schedulerProvider: SchedulerProvider
) : IRemoteDataSource {

    @MainThread
    fun fetchReposByPage(
        username: String,
        sort: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<List<Repo>> {
        return fetchReposByPageInternal(username, sort, pageIndex, perPage)
    }

    @MainThread
    private fun fetchReposByPageInternal(
        username: String,
        sort: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<List<Repo>> {
        return serviceManager.userService
            .queryRepos(username, pageIndex, perPage, sort)
            .subscribeOn(schedulerProvider.io())
    }
}

class LocalReposDataSource(
    private val db: UserDatabase,
    private val schedulers: SchedulerProvider
) : ILocalDataSource {

    @MainThread
    fun fetchPagedListFromDb(
        boundaryCallback: PagedList.BoundaryCallback<Repo>
    ): Flowable<PagedList<Repo>> {
        return db.userReposDao().queryRepos()
            .toRxPagedList(
                boundaryCallback = boundaryCallback,
                fetchSchedulers = schedulers.io()
            )
    }

    @WorkerThread
    fun clearOldData() {
        db.runInTransaction {
            db.userReposDao().deleteAllRepos()
        }
    }

    @AnyThread
    fun insertNewPagedEventData(newPage: List<Repo>): Completable {
        return Completable
            .fromAction { insertDataInternal(newPage) }
            .subscribeOn(schedulers.io())
    }

    private fun insertDataInternal(newPage: List<Repo>) {
        db.runInTransaction {
            val start = db.userReposDao().getNextIndexInRepos()
            val items = newPage.mapIndexed { index, child ->
                child.indexInSortResponse = start + index
                child
            }
            db.userReposDao().insert(items)
        }
    }
}