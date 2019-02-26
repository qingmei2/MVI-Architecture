package com.github.qingmei2.sample.ui.main.repos

import arrow.core.Either
import com.github.qingmei2.mvi.base.repository.BaseRepositoryBoth
import com.github.qingmei2.mvi.base.repository.ILocalDataSource
import com.github.qingmei2.mvi.base.repository.IRemoteDataSource
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.Repo
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import io.reactivex.Completable
import io.reactivex.Flowable

class ReposDataSource(
    remote: RemoteReposDataSource,
    local: LocalReposDataSource
) : BaseRepositoryBoth<RemoteReposDataSource, LocalReposDataSource>(remote, local) {

    fun queryRepos(
        username: String,
        pageIndex: Int,
        perPage: Int,
        sort: String
    ): Flowable<Either<Errors, List<Repo>>> =
        remoteDataSource.queryRepos(username, pageIndex, perPage, sort)
            .flatMap { reposEither ->
                localDataSource.saveReposToLocal(reposEither)
                    .andThen(Flowable.just(reposEither))
            }
}

class RemoteReposDataSource(
    private val serviceManager: ServiceManager,
    private val schedulerProvider: SchedulerProvider
) : IRemoteDataSource {

    fun queryRepos(
        username: String,
        pageIndex: Int,
        perPage: Int,
        sort: String
    ): Flowable<Either<Errors, List<Repo>>> {
        return serviceManager.userService
            .queryRepos(username, pageIndex, perPage, sort)
            .subscribeOn(schedulerProvider.io())
            .map {
                when (it.isEmpty()) {
                    true -> Either.left(Errors.EmptyResultsError)
                    false -> Either.right(it)
                }
            }
    }
}

class LocalReposDataSource : ILocalDataSource {

    fun saveReposToLocal(repos: Either<Errors, List<Repo>>): Completable {
        return Completable.complete()
    }
}