package com.github.qingmei2.sample.di

import com.github.qingmei2.mvi.di.ActivityScope
import com.github.qingmei2.sample.di.activity.LoginActivityModule
import com.github.qingmei2.sample.di.activity.MainActivityModule
import com.github.qingmei2.sample.ui.login.LoginActivity
import com.github.qingmei2.sample.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [LoginActivityModule::class])
    abstract fun contributesLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun contributesMainActivity(): MainActivity
}