package com.github.qingmei2.sample.di.fragment

import androidx.lifecycle.ViewModelProviders
import com.github.qingmei2.mvi.di.FragmentScope
import com.github.qingmei2.sample.db.UserDatabase
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.service.ServiceManager
import com.github.qingmei2.sample.ui.main.profile.*
import dagger.Module
import dagger.Provides

@Module
class ProfileFragmentModule {

    @FragmentScope
    @Provides
    fun providesViewModel(
        fragment: ProfileFragment,
        processorHolder: ProfileActionProcessorHolder
    ): ProfileViewModel {
        return ViewModelProviders
            .of(fragment, ProfileViewModelFactory(processorHolder))[ProfileViewModel::class.java]
    }

    @FragmentScope
    @Provides
    fun providesProfileRepository(
        serviceManager: ServiceManager,
        schedulerProvider: SchedulerProvider,
        database: UserDatabase
    ): ProfileRepository {
        return ProfileRepository(
            remoteDataSource = ProfileRemoteDataSource(serviceManager)
        )
    }

    @FragmentScope
    @Provides
    fun providesProfileActionProcessorHolder(
        repository: ProfileRepository,
        schedulerProvider: SchedulerProvider
    ): ProfileActionProcessorHolder {
        return ProfileActionProcessorHolder(repository, schedulerProvider)
    }
}