package com.github.qingmei2.sample.di

import com.github.qingmei2.mvi.di.FragmentScope
import com.github.qingmei2.sample.di.fragment.HomeFragmentModule
import com.github.qingmei2.sample.di.fragment.ProfileFragmentModule
import com.github.qingmei2.sample.di.fragment.ReposFragmentModule
import com.github.qingmei2.sample.ui.main.home.HomeFragment
import com.github.qingmei2.sample.ui.main.profile.ProfileFragment
import com.github.qingmei2.sample.ui.main.repos.ReposFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun contributesHomeFragment(): HomeFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ReposFragmentModule::class])
    abstract fun contributesReposFragment(): ReposFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun contributesProfileFragment(): ProfileFragment
}