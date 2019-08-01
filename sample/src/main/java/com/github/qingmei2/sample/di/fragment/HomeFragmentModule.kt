package com.github.qingmei2.sample.di.fragment

import androidx.lifecycle.ViewModelProviders
import com.github.qingmei2.mvi.di.FragmentScope
import com.github.qingmei2.sample.db.UserDatabase
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import com.github.qingmei2.sample.repository.UserInfoRepository
import com.github.qingmei2.sample.ui.main.home.*
import dagger.Module
import dagger.Provides

@Module
class HomeFragmentModule {

    @FragmentScope
    @Provides
    fun providesViewModel(
        fragment: HomeFragment,
        processorHolder: HomeActionProcessorHolder
    ): HomeViewModel {
        return ViewModelProviders
            .of(fragment, HomeViewModelFactory(processorHolder))[HomeViewModel::class.java]
    }

    @FragmentScope
    @Provides
    fun providesHomeActionProcessorHolder(
        repository: HomeRepository,
        userRepository: UserInfoRepository,
        schedulerProvider: SchedulerProvider
    ): HomeActionProcessorHolder {
        return HomeActionProcessorHolder(repository, userRepository, schedulerProvider)
    }

    @FragmentScope
    @Provides
    fun providesHomeRepository(
        serviceManager: ServiceManager,
        schedulerProvider: SchedulerProvider,
        database: UserDatabase
    ): HomeRepository {
        return HomeRepository(
            remoteDataSource = HomeRemoteDataSource(serviceManager, schedulerProvider),
            localDataSource = HomeLocalDataSource(database, schedulerProvider)
        )
    }
}