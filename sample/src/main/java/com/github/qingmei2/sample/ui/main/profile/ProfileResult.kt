package com.github.qingmei2.sample.ui.main.profile

import com.github.qingmei2.sample.entity.LoginUser

sealed class ProfileResult {

    sealed class InitialResult : ProfileResult() {
        data class Success(val user: LoginUser) : InitialResult()
        data class Failure(val error: Throwable) : InitialResult()
        object InFlight : InitialResult()
    }
}