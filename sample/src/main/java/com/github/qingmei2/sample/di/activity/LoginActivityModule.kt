package com.github.qingmei2.sample.di.activity

import androidx.lifecycle.ViewModelProviders
import com.github.qingmei2.mvi.di.ActivityScope
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import com.github.qingmei2.sample.repository.UserInfoRepository
import com.github.qingmei2.sample.ui.login.*
import dagger.Module
import dagger.Provides

@Module
class LoginActivityModule {

    @ActivityScope
    @Provides
    fun providesViewModel(
        activity: LoginActivity,
        processorHolder: LoginActionProcessorHolder
    ): LoginViewModel {
        return ViewModelProviders
            .of(activity, LoginViewModelFactory(processorHolder))[LoginViewModel::class.java]
    }

    @ActivityScope
    @Provides
    fun providesLoginActionProcessorHolder(
        repository: LoginDataSourceRepository,
        schedulerProvider: SchedulerProvider
    ): LoginActionProcessorHolder {
        return LoginActionProcessorHolder(repository, schedulerProvider)
    }

    @ActivityScope
    @Provides
    fun providesLoginRepository(
        serviceManager: ServiceManager,
        schedulerProvider: SchedulerProvider,
        userRepo: UserInfoRepository
    ): LoginDataSourceRepository {
        return LoginDataSourceRepository(
            remoteDataSource = LoginRemoteDataSource(serviceManager, schedulerProvider),
            localDataSource = LoginLocalDataSource(userRepo)
        )
    }
}