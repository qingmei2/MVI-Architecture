package com.github.qingmei2.sample.di

import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.scheduler.SchedulerProviderProxy
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

private const val SCHEDULERS_MODULE_TAG = "schedulers_module_tag"

val schedulersModule = Kodein.Module(SCHEDULERS_MODULE_TAG) {

    bind<SchedulerProvider>() with singleton {
        SchedulerProviderProxy()
    }
}