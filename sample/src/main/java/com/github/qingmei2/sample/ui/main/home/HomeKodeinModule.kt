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
            .of(
                context,
                HomeViewModelFactory.getInstance(instance())
            )[HomeViewModel::class.java]
    }

    bind<HomeRepository>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        HomeRepository(instance())
    }

    bind<HomeRemoteDataSource>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        HomeRemoteDataSource(instance())
    }

    bind<HomeActionProcessorHolder>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        HomeActionProcessorHolder(instance(), instance())
    }
}