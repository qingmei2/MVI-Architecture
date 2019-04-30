package com.github.qingmei2.sample.ui.main.home

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

val homeKodeinModule = Kodein.Module("homeKodeinModule") {

    bind<HomeViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        ViewModelProviders
            .of(context, HomeViewModelFactory.getInstance(instance()))[HomeViewModel::class.java]
    }

    bind<HomeRepository>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        HomeRepository(
            remoteDataSource = instance(),
            localDataSource = instance()
        )
    }

    bind<HomeRemoteDataSource>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        HomeRemoteDataSource(
            serviceManager = instance(),
            schedulers = instance()
        )
    }

    bind<HomeLocalDataSource>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        HomeLocalDataSource(
            db = instance(),
            schedulers = instance()
        )
    }

    bind<HomeActionProcessorHolder>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        HomeActionProcessorHolder(
            repository = instance(),
            userRepository = instance(),
            schedulerProvider = instance()
        )
    }
}