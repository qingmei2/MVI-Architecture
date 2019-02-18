package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.sample.entity.LoginEntity
import com.github.qingmei2.sample.entity.LoginUser

sealed class LoginResult {

    sealed class AutoLoginInfoResult : LoginResult() {
        data class Success(val user: LoginEntity, val autoLogin: Boolean) : AutoLoginInfoResult()
        data class Failure(val error: Throwable) : AutoLoginInfoResult()
        object NoUserData : AutoLoginInfoResult()
        object InFlight : AutoLoginInfoResult()
    }

    sealed class ClickLoginResult : LoginResult() {
        data class Success(val user: LoginUser) : ClickLoginResult()
        data class Failure(val error: Throwable) : ClickLoginResult()
        object InFlight : ClickLoginResult()
    }
}