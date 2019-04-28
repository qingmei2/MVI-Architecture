package com.github.qingmei2.sample.ui.main.profile

import com.github.qingmei2.sample.entity.UserInfo

sealed class ProfileResult {

    sealed class InitialResult : ProfileResult() {
        data class Success(val user: UserInfo) : InitialResult()
        data class Failure(val error: Throwable) : InitialResult()
        object InFlight : InitialResult()
    }
}