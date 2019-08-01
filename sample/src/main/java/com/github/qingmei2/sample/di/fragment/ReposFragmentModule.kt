package com.github.qingmei2.sample.di.fragment

import androidx.lifecycle.ViewModelProviders
import com.github.qingmei2.mvi.di.FragmentScope
import com.github.qingmei2.sample.db.UserDatabase
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import com.github.qingmei2.sample.repository.UserInfoRepository
import com.github.qingmei2.sample.ui.main.repos.*
import dagger.Module
import dagger.Provides

@Module
class ReposFragmentModule {

    @FragmentScope
    @Provides
    fun providesViewModel(
        fragment: ReposFragment,
        processorHolder: ReposActionProcessorHolder
    ): ReposViewModel {
        return ViewModelProviders
            .of(fragment, ReposViewModelFactory(processorHolder))[ReposViewModel::class.java]
    }

    @FragmentScope
    @Provides
    fun providesReposActionProcessorHolder(
        repository: ReposRepository,
        userRepository: UserInfoRepository,
        schedulerProvider: SchedulerProvider
    ): ReposActionProcessorHolder {
        return ReposActionProcessorHolder(repository, userRepository, schedulerProvider)
    }

    @FragmentScope
    @Provides
    fun providesRepoRepository(
        serviceManager: ServiceManager,
        schedulerProvider: SchedulerProvider,
        database: UserDatabase
    ): ReposRepository {
        return ReposRepository(
            remote = RemoteReposDataSource(serviceManager, schedulerProvider),
            local = LocalReposDataSource(database, schedulerProvider)
        )
    }
}