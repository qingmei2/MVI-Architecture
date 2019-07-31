package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.repository.BaseRepositoryBoth
import com.github.qingmei2.mvi.base.repository.ILocalDataSource
import com.github.qingmei2.mvi.base.repository.IRemoteDataSource
import com.github.qingmei2.sample.entity.UserInfo
import com.github.qingmei2.sample.entity.event.AutoLoginEvent
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import com.github.qingmei2.sample.http.service.bean.LoginRequestModel
import com.github.qingmei2.sample.manager.UserManager
import com.github.qingmei2.sample.repository.UserInfoRepository
import io.reactivex.Completable
import io.reactivex.Flowable

class LoginDataSourceRepository(
    remoteDataSource: LoginRemoteDataSource,
    localDataSource: LoginLocalDataSource
) : BaseRepositoryBoth<LoginRemoteDataSource, LoginLocalDataSource>(remoteDataSource, localDataSource) {

    fun login(username: String, password: String): Flowable<UserInfo> {
        // 保存用户登录信息
        return localDataSource.savePrefsUser(username, password)
            .andThen(remoteDataSource.login())
            .doOnNext { info -> UserManager.INSTANCE = info }
            // 如果登录失败，清除登录信息
            .doOnError { localDataSource.clearPrefsUser() }
    }

    fun fetchAutoLogin(): Flowable<AutoLoginEvent> {
        return localDataSource.fetchAutoLogin()
    }
}

class LoginRemoteDataSource(
    private val serviceManager: ServiceManager,
    private val schedulers: SchedulerProvider
) : IRemoteDataSource {

    fun login(): Flowable<UserInfo> {
        val authObservable = serviceManager.loginService
            .authorizations(LoginRequestModel.generate())

        val ownerInfoObservable = serviceManager.userService
            .fetchUserOwner()

        return authObservable                       // 1.用户登录认证
            .flatMap { ownerInfoObservable }        // 2.获取用户详细信息
            .subscribeOn(schedulers.io())
    }
}

class LoginLocalDataSource(
    private val userRepository: UserInfoRepository
) : ILocalDataSource {

    fun savePrefsUser(username: String, password: String): Completable {
        return Completable.fromAction {
            userRepository.username = username
            userRepository.password = password
        }
    }

    fun clearPrefsUser(): Completable {
        return Completable.fromAction {
            userRepository.username = ""
            userRepository.password = ""
        }
    }

    fun fetchAutoLogin(): Flowable<AutoLoginEvent> {
        val username = userRepository.username
        val password = userRepository.password
        val isAutoLogin = userRepository.isAutoLogin
        return Flowable.just(
            when (username.isNotEmpty() && password.isNotEmpty() && isAutoLogin) {
                true -> AutoLoginEvent(true, username, password)
                false -> AutoLoginEvent(false, "", "")
            }
        )
    }
}