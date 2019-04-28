package com.github.qingmei2.sample.ui.login

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val LOGIN_MODULE_TAG = "LOGIN_MODULE_TAG"

val loginKodeinModule = Kodein.Module(LOGIN_MODULE_TAG) {

    bind<LoginViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        ViewModelProviders
            .of(context, LoginViewModelFactory.getInstance(instance()))[LoginViewModel::class.java]
    }

    bind<LoginActionProcessorHolder>() with singleton {
        LoginActionProcessorHolder(
            repository = instance(),
            schedulers = instance()
        )
    }

    bind<LoginRemoteDataSource>() with singleton {
        LoginRemoteDataSource(
            serviceManager = instance(),
            schedulers = instance()
        )
    }

    bind<LoginLocalDataSource>() with singleton {
        LoginLocalDataSource(userRepository = instance())
    }

    bind<LoginDataSourceRepository>() with singleton {
        LoginDataSourceRepository(instance(), instance())
    }
}