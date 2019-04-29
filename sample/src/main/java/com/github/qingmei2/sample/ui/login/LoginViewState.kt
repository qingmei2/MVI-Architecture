package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.viewstate.IViewState
import com.github.qingmei2.sample.entity.UserInfo

data class LoginViewState(
    val isLoading: Boolean,
    val errors: Throwable?,
    val uiEvents: LoginUiEvents?
) : IViewState {

    sealed class LoginUiEvents {

        data class JumpMain(val loginUser: UserInfo) : LoginUiEvents()

        data class TryAutoLogin(
            val username: String,
            val password: String,
            val autoLogin: Boolean
        ) : LoginUiEvents()
    }

    companion object {

        fun idle(): LoginViewState = LoginViewState(
            isLoading = false,
            errors = null,
            uiEvents = null
        )
    }
}