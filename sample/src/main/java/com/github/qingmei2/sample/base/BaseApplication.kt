package com.github.qingmei2.sample.base

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.github.qingmei2.mvi.logger.initLogger
import com.github.qingmei2.sample.BuildConfig
import com.github.qingmei2.sample.di.*
import com.squareup.leakcanary.LeakCanary
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

open class BaseApplication : Application(), KodeinAware {

    override val kodein: Kodein = Kodein.lazy {
        bind<Context>() with singleton { this@BaseApplication }
        import(androidModule(this@BaseApplication))
        import(androidXModule(this@BaseApplication))

        import(serviceModule)
        import(dbModule)
        import(httpClientModule)
        import(prefsModule)
        import(schedulersModule)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        initLogger(BuildConfig.DEBUG)
        
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