package com.github.qingmei2.mvi.base.repository

open class BaseRepositoryBoth<T : IRemoteDataSource, R : ILocalDataSource>(
        val remoteDataSource: T,
        val localDataSource: R
) : IRepository

open class BaseRepositoryLocal<T : ILocalDataSource>(
        val remoteDataSource: T
) : IRepository

open class BaseRepositoryRemote<T : IRemoteDataSource>(
        val remoteDataSource: T
) : IRepository

open class BaseRepositoryNothing() : IRepository