package com.github.qingmei2.sample.base

import android.app.Application
import com.facebook.stetho.Stetho
import com.github.qingmei2.mvi.logger.initLogger
import com.github.qingmei2.sample.BuildConfig
import com.github.qingmei2.sample.di.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

open class BaseApplication : Application(),HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        initLogger(BuildConfig.DEBUG)

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)

        if (BuildConfig.DEBUG) {
            initStetho()
            initLeakCanary()
        }
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }

    companion object {
        lateinit var INSTANCE: BaseApplication
    }
}