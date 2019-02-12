package com.github.qingmei2.sample.ui.login

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
            .of(context, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return LoginViewModel(instance()) as T
                }
            })
            .get(LoginViewModel::class.java)
    }

    bind<LoginRemoteDataSource>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        LoginRemoteDataSource(
            serviceManager = instance(),
            schedulers = instance()
        )
    }

    bind<LoginLocalDataSource>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        LoginLocalDataSource(
            prefs = instance()
        )
    }

    bind<LoginDataSourceRepository>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        LoginDataSourceRepository(instance(), instance())
    }
}