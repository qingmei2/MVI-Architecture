package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.viewstate.IViewState
import com.github.qingmei2.sample.entity.LoginEntity
import com.github.qingmei2.sample.entity.LoginUser

data class LoginViewState(
        val isLoading: Boolean,
        val errors: Throwable?,
        val uiEvents: LoginUiEvents?
) : IViewState {

    sealed class LoginUiEvents {

        data class JumpMain(val loginUser: LoginUser) : LoginUiEvents()

        data class TryAutoLogin(
                val loginEntity: LoginEntity,
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