package com.github.qingmei2.sample.ui.main.repos

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

val reposKodeinModule = Kodein.Module("reposKodeinModule") {

    bind<ReposViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        ViewModelProviders
            .of(context, ReposViewModelFactory.getInstance(instance()))[ReposViewModel::class.java]
    }

    bind<LocalReposDataSource>() with singleton {
        LocalReposDataSource()
    }

    bind<RemoteReposDataSource>() with singleton {
        RemoteReposDataSource(
            serviceManager = instance(),
            schedulerProvider = instance()
        )
    }

    bind<ReposDataSource>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        ReposDataSource(
            remote = instance(),
            local = instance()
        )
    }

    bind<ReposActionProcessorHolder>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        ReposActionProcessorHolder(
            repository = instance(),
            schedulerProvider = instance()
        )
    }
}