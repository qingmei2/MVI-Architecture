package com.github.qingmei2.sample.di

import com.github.qingmei2.sample.repository.UserInfoRepository
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val GLOBAL_REPOS_MODULE_TAG = "globalRepositoryModule"

val globalRepositoryModule = Kodein.Module(GLOBAL_REPOS_MODULE_TAG) {

    bind<UserInfoRepository>() with singleton {
        UserInfoRepository.getInstance(instance())
    }
}