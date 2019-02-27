package com.github.qingmei2.sample.ui.main.profile

import com.github.qingmei2.mvi.base.repository.BaseRepositoryRemote
import com.github.qingmei2.mvi.base.repository.IRemoteDataSource
import com.github.qingmei2.sample.http.service.ServiceManager

class ProfileRepository(
    remoteDataSource: ProfileRemoteDataSource
) : BaseRepositoryRemote<ProfileRemoteDataSource>(remoteDataSource)

class ProfileRemoteDataSource(val serviceManager: ServiceManager) : IRemoteDataSource