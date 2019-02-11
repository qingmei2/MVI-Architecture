package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.viewstate.IViewState

data class LoginViewState(
        val editUsername: String,
        val editPassword: String,
        val autoLogin: Boolean,
        val isLoading: Boolean,
        val errors: Throwable?,
        val uiEvents: LoginUiEvents?
) : IViewState {

    enum class LoginUiEvents {

        JUMP_MAIN
    }

    companion object {

        fun idle(): LoginViewState {
            return LoginViewState(
                    editUsername = "",
                    editPassword = "",
                    autoLogin = true,
                    isLoading = false,
                    errors = null,
                    uiEvents = null
            )
        }
    }
}