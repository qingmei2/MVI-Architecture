package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.sample.entity.UserInfo

sealed class LoginResult {

    sealed class AutoLoginInfoResult : LoginResult() {
        data class Success(
            val username: String,
            val password: String,
            val autoLogin: Boolean
        ) : AutoLoginInfoResult()

        data class Failure(val error: Throwable) : AutoLoginInfoResult()
        object NoUserData : AutoLoginInfoResult()
        object InFlight : AutoLoginInfoResult()
    }

    sealed class ClickLoginResult : LoginResult() {
        data class Success(val user: UserInfo) : ClickLoginResult()
        data class Failure(val error: Throwable) : ClickLoginResult()
        object InFlight : ClickLoginResult()
    }
}